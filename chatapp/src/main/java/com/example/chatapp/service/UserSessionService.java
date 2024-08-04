package com.example.chatapp.service;

import com.example.chatapp.model.Users;
import com.example.chatapp.repositopry.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSessionService {

    private final UserRepository userRepository;

    public UserSessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isUserConnected(String username) {
        Users user = userRepository.findByUsername(username);
        return user != null && user.isConnected();
    }
    public List<Users> getConnectedUsers() {
        return userRepository.findAll().stream()
                .filter(Users::isConnected)
                .collect(Collectors.toList());
    }

    public List<Users> getDisconnectedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isConnected())
                .collect(Collectors.toList());
    }
}

