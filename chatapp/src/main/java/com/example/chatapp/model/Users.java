package com.example.chatapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(unique = true)
    private String username;

    @NotEmpty
    private String password;

    private boolean connected;

    private String jwtToken;

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "user_id")
    )
    private Set<Users> friends = new HashSet<>();

    @OneToMany(mappedBy = "requester")
    private Set<FriendRequest> sentRequests = new HashSet<>();

    @OneToMany(mappedBy = "requested")
    private Set<FriendRequest> receivedRequests = new HashSet<>();

    public boolean isConnected() {
        return connected;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Long getId() {
        return user_id;
    }

    public void setId(Long id) {
        this.user_id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public @NotEmpty String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty String password) {
        this.password = password;
    }

    public Set<Users> getFriends() {
        return friends;
    }

    public void setFriends(Set<Users> friends) {
        this.friends = friends;
    }

    public Set<FriendRequest> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(Set<FriendRequest> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public Set<FriendRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(Set<FriendRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }
}
