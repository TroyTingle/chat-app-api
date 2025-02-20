package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.friends.AddFriendRequest;
import com.ttingle.chat_app_api.model.FriendRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.FriendRequestService;
import com.ttingle.chat_app_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friend-request")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    @Autowired
    public FriendRequestController(FriendRequestService friendRequestService, UserService userService) {
        this.friendRequestService = friendRequestService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddFriendRequest friendRequest) {
        User sender = userService.findByUsername(userDetails.getUsername());
        User recipient = userService.findByUsername(friendRequest.getUsername());
        friendRequestService.sendFriendRequest(sender, recipient);
        return new ResponseEntity<>("Friend request sent", HttpStatus.OK);
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID requestId) {
        User recipient = userService.findByUsername(userDetails.getUsername());
        friendRequestService.respondToFriendRequest(recipient, requestId, true);
        return new ResponseEntity<>("Friend request accepted", HttpStatus.OK);
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID requestId) {
        User recipient = userService.findByUsername(userDetails.getUsername());
        friendRequestService.respondToFriendRequest(recipient, requestId, false);
        return new ResponseEntity<>("Friend request rejected", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FriendRequest>> getFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsForUser(user);
        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }
}