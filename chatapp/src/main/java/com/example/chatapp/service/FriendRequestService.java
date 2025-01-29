package com.example.chatapp.service;

import com.example.chatapp.model.FriendRequest;
import com.example.chatapp.model.Users;
import com.example.chatapp.repositopry.FriendRequestRepository;
import com.example.chatapp.repositopry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository usersRepository;

    public void sendFriendRequest(Users requester, Users requested) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(requester);
        friendRequest.setRequested(requested);
        friendRequestRepository.save(friendRequest);
    }


}
