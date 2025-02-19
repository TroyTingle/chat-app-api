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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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
        User sender = new User();
        sender.setUsername("sender");

        User recipient = new User();
        recipient.setUsername("recipient");

        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUsername(recipient.getUsername());

        when(userService.findByUsername(anyString())).thenReturn(recipient);

        ResponseEntity<String> response = friendRequestController.sendFriendRequest(sender, addFriendRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request sent", response.getBody());
        verify(friendRequestService, times(1)).sendFriendRequest(sender, recipient);
    }

    @Test
    void testAcceptFriendRequest_Success() {
        User recipient = new User();
        recipient.setUsername("recipient");
        UUID id = UUID.randomUUID();

        ResponseEntity<String> response = friendRequestController.acceptFriendRequest(recipient, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request accepted", response.getBody());
        verify(friendRequestService, times(1)).respondToFriendRequest(recipient, id, true);
    }

    @Test
    void testRejectFriendRequest_Success() {
        User recipient = new User();
        recipient.setUsername("recipient");

        UUID id = UUID.randomUUID();

        ResponseEntity<String> response = friendRequestController.rejectFriendRequest(recipient, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request rejected", response.getBody());
        verify(friendRequestService, times(1)).respondToFriendRequest(recipient, id, false);
    }

    @Test
    void testGetFriendRequests_Success() {
        User user = new User();
        user.setUsername("user");

        List<FriendRequest> friendRequests = List.of(new FriendRequest(), new FriendRequest());
        when(friendRequestService.getFriendRequestsForUser(user)).thenReturn(friendRequests);

        ResponseEntity<List<FriendRequest>> response = friendRequestController.getFriendRequests(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendRequests, response.getBody());
    }
}