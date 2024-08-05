package com.example.chatapp.controller;

import com.example.chatapp.service.UserService;
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
}
