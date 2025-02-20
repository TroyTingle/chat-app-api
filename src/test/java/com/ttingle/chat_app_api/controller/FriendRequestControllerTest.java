package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.friends.AddFriendRequest;
import com.ttingle.chat_app_api.model.FriendRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.FriendRequestService;
import com.ttingle.chat_app_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestControllerTest {

    @Mock
    private FriendRequestService friendRequestService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendRequestController friendRequestController;

    @Test
    void testSendFriendRequest_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User sender = new User();

        User recipient = new User();
        recipient.setUsername("recipient");

        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUsername(recipient.getUsername());

        when(userService.findByUsername(user.getUsername())).thenReturn(sender);
        when(userService.findByUsername("recipient")).thenReturn(recipient);

        ResponseEntity<String> response = friendRequestController.sendFriendRequest(user, addFriendRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request sent", response.getBody());
        verify(friendRequestService, times(1)).sendFriendRequest(sender, recipient);
    }

    @Test
    void testAcceptFriendRequest_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User recipient = new User();
        UUID id = UUID.randomUUID();

        when(userService.findByUsername(user.getUsername())).thenReturn(recipient);

        ResponseEntity<String> response = friendRequestController.acceptFriendRequest(user, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request accepted", response.getBody());
        verify(friendRequestService, times(1)).respondToFriendRequest(recipient, id, true);
    }

    @Test
    void testRejectFriendRequest_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User recipient = new User();

        UUID id = UUID.randomUUID();

        when(userService.findByUsername(user.getUsername())).thenReturn(recipient);

        ResponseEntity<String> response = friendRequestController.rejectFriendRequest(user, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request rejected", response.getBody());
        verify(friendRequestService, times(1)).respondToFriendRequest(recipient, id, false);
    }

    @Test
    void testGetFriendRequests_Success() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User user = new User();

        List<FriendRequest> friendRequests = List.of(new FriendRequest(), new FriendRequest());

        when(userService.findByUsername(userDetails.getUsername())).thenReturn(user);
        when(friendRequestService.getFriendRequestsForUser(user)).thenReturn(friendRequests);

        ResponseEntity<List<FriendRequest>> response = friendRequestController.getFriendRequests(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendRequests, response.getBody());
    }
}