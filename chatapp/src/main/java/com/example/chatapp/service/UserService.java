package com.example.chatapp.service;

import com.example.chatapp.model.Users;
import com.example.chatapp.repositopry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Users user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists with username: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public void userConnected(String username, String token) {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            user.setConnected(true);
            user.setJwtToken(token); // Guarda el token en el usuario
            userRepository.save(user);
            sendUpdate();
        }
    }
    public List<String> getConnectedUsers() {
        return userRepository.findAll().stream()
                .filter(Users::isConnected)
                .map(Users::getUsername)
                .collect(Collectors.toList());
    }

    public void userDisconnected(String username) {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("Disconnecting user: " + username);
            user.setConnected(false);
            user.setJwtToken(null); // Elimina el token al desconectar
            userRepository.save(user);
            System.out.println("User status updated in database");
            sendUpdate();
        } else {
            System.out.println("User not found: " + username);
        }
    }

    public String getTokenForUser(String username) {
        Users user = userRepository.findByUsername(username);
        return (user != null) ? user.getJwtToken() : null;
    }

    public void saveTokenForUser(String username, String token) {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            user.setJwtToken(token);
            userRepository.save(user);

        }
    }
    private void sendUpdate() {
        List<String> connectedUsers = getConnectedUsers();
        System.out.println("Connected users: " + connectedUsers);
        messagingTemplate.convertAndSend("/topic/connectedUsers", connectedUsers);
    }

}

