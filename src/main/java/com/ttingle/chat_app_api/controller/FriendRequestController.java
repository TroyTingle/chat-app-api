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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal User sender, @RequestBody AddFriendRequest friendRequest) {
        User recipient = userService.findByUsername(friendRequest.getUsername());
        friendRequestService.sendFriendRequest(sender, recipient);
        return new ResponseEntity<>("Friend request sent", HttpStatus.OK);
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal User recipient, @PathVariable Long requestId) {
        friendRequestService.respondToFriendRequest(recipient, requestId, true);
        return new ResponseEntity<>("Friend request accepted", HttpStatus.OK);
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(@AuthenticationPrincipal User recipient, @PathVariable Long requestId) {
        friendRequestService.respondToFriendRequest(recipient, requestId, false);
        return new ResponseEntity<>("Friend request rejected", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FriendRequest>> getFriendRequests(@AuthenticationPrincipal User user) {
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsForUser(user);
        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }
}