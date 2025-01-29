package com.example.chatapp.controller;

import com.example.chatapp.controller.entity.AuthRequest;
import com.example.chatapp.controller.entity.AuthResponse;
import com.example.chatapp.model.Users;
import com.example.chatapp.service.JwtUtil;
import com.example.chatapp.service.TokenBlacklistService;
import com.example.chatapp.service.UserAlreadyExistsException;
import com.example.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class AuthController {

    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, TokenBlacklistService tokenBlacklistService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authenticationManager = authenticationManager;
    }



    @MessageMapping("/logout")
    @SendToUser("/queue/logout")
    public ResponseEntity<?> logout(@Header("Authorization") String authHeader) {
        try {
            System.out.println("Received auth header: " + authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                String username = JwtUtil.getUsernameFromToken(token); // Ensure token is passed correctly
                System.out.println("Logged out user: " + username);
                tokenBlacklistService.addToBlacklist(token);
                userService.userDisconnected(username); // Elimina la sesión activa
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Authorization header");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }




    @MessageMapping("/register")
    @SendToUser("/queue/register")
    public ResponseEntity<?> register(Users users) {
        try {
            userService.register(users);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @MessageMapping("/login")
    @SendToUser("/queue/login")
    public ResponseEntity<?> login(AuthRequest authRequest) {
        String token = "";
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Logged in user: " + authentication);
            String username = authRequest.getUsername();
            if (authentication.isAuthenticated()) {
                String existingToken = userService.getTokenForUser(username);
                if (existingToken == null || JwtUtil.isTokenExpired(existingToken)) {
                    token = JwtUtil.generateToken(username);
                    userService.saveTokenForUser(username, token);
                } else {
                    token = existingToken;
                }
                userService.userConnected(username, token);
                System.out.println("User connected: " + username);
            }

            return ResponseEntity.ok(new AuthResponse("Login successful", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @SendToUser("/queue/error")
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
