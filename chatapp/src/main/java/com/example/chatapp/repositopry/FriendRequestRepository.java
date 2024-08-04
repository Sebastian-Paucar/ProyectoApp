package com.example.chatapp.repositopry;

import com.example.chatapp.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
}