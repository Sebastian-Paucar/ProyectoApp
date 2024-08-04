package com.example.chatapp.model;

import jakarta.persistence.*;
import java.security.SecureRandom;
import java.util.Base64;

@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id")
    private Users requester;

    @ManyToOne
    @JoinColumn(name = "requested_id", referencedColumnName = "user_id")
    private Users requested;

    @Column(unique = true)
    private String chatid;

    private boolean accepted;

    public FriendRequest() {
        this.chatid = generateBase32Id();
        this.accepted = false;
    }

    private String generateBase32Id() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 20 bytes = 160 bits, enough for a base32 string
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes).substring(0, 16); // base32 string without padding
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Users getRequester() {
        return requester;
    }

    public void setRequester(Users requester) {
        this.requester = requester;
    }

    public Users getRequested() {
        return requested;
    }

    public void setRequested(Users requested) {
        this.requested = requested;
    }

    public String getChatid() {
        return chatid;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
