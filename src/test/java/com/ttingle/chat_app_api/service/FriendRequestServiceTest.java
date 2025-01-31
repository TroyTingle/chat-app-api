package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.FriendRequestException;
import com.ttingle.chat_app_api.exceptions.NotFoundException;
import com.ttingle.chat_app_api.model.FriendRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.FriendRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendRequestService friendRequestService;

    @Test
    public void testSendFriendRequest_Success() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);

        assertDoesNotThrow(() -> friendRequestService.sendFriendRequest(sender, receiver));

        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    public void testSendFriendRequest_RequestAlreadyExists() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(true);

        assertThrows(FriendRequestException.class, () -> friendRequestService.sendFriendRequest(sender, receiver));
    }

    @Test
    public void testRespondToFriendRequest_Accept() {
        User sender = new User();
        User receiver = new User();
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertDoesNotThrow(() -> friendRequestService.respondToFriendRequest(receiver,1L, true));

        assertEquals(FriendRequest.RequestStatus.ACCEPTED, request.getStatus());
        assertTrue(sender.getFriends().contains(receiver));
        assertTrue(receiver.getFriends().contains(sender));
        verify(friendRequestRepository, times(1)).save(request);
        verify(userService, times(1)).saveUser(sender);
        verify(userService, times(1)).saveUser(receiver);
    }

    @Test
    public void testRespondToFriendRequest_Reject() {
        User receiver = new User();
        FriendRequest request = new FriendRequest();
        request.setReceiver(receiver);

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertDoesNotThrow(() -> friendRequestService.respondToFriendRequest(receiver, 1L, false));

        assertEquals(FriendRequest.RequestStatus.REJECTED, request.getStatus());
        verify(friendRequestRepository, times(1)).save(request);
    }

    @Test
    public void testRespondToFriendRequest_NotFound() {
        User receiver = new User();

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> friendRequestService.respondToFriendRequest(receiver, 1L, true));
    }

    @Test
    public void testGetFriendRequestsForUser() {
        User user = new User();
        FriendRequest request1 = new FriendRequest();
        FriendRequest request2 = new FriendRequest();
        List<FriendRequest> requests = Arrays.asList(request1, request2);

        when(friendRequestRepository.findAllByReceiver(user)).thenReturn(requests);

        List<FriendRequest> result = friendRequestService.getFriendRequestsForUser(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(request1));
        assertTrue(result.contains(request2));
        verify(friendRequestRepository, times(1)).findAllByReceiver(user);
    }

    @Test
    public void testGetFriendRequestsForUser_NoRequests() {
        User user = new User();

        when(friendRequestRepository.findAllByReceiver(user)).thenReturn(List.of());

        List<FriendRequest> result = friendRequestService.getFriendRequestsForUser(user);

        assertTrue(result.isEmpty());
        verify(friendRequestRepository, times(1)).findAllByReceiver(user);
    }
}