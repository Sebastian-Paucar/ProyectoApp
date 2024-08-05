package com.example.chatapp.controller;

import com.example.chatapp.service.JwtUtil;
import com.example.chatapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private final UserService userSessionService;

    public UserController(UserService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @MessageMapping("/connect")
    @SendToUser("/queue/connect")
    public List<String> getConnectedUsers() {
        try {
            List<String> connectedUsers = userSessionService.getConnectedUsers();
            System.out.println("Prueba de usuarios: " + connectedUsers);
            return connectedUsers;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("An error occurred while fetching connected users");
        }
    }
    @MessageMapping("/getUsername")
    @SendToUser("/queue/getUsername")
    public ResponseEntity<?> getUsernameFromToken(@Header("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                String username = JwtUtil.getUsernameFromToken(token);
                System.out.println("Logged in user: " + username);
                return ResponseEntity.ok(username);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Authorization header");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the username");
        }
    }

}
