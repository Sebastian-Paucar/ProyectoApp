package com.example.chatapp.controller;

import com.example.chatapp.model.Users;
import com.example.chatapp.service.UserSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
public class UserController {

    private final UserSessionService userSessionService;

    public UserController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }
    @MessageMapping("/user/connect")
    @SendToUser("/session/connect")
    public ResponseEntity<?> getConnectedUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    List<Users> connectedUsers = userSessionService.getConnectedUsers();
                    return ResponseEntity.ok(connectedUsers);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header missing or invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching connected users");
        }
    }

    @MessageMapping("/user/disconnected")
    @SendToUser("/session/disconnected")
    public ResponseEntity<?> getDisconnectedUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    List<Users> disconnectedUsers = userSessionService.getDisconnectedUsers();
                    return ResponseEntity.ok(disconnectedUsers);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header missing or invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching disconnected users");
        }
    }

}

