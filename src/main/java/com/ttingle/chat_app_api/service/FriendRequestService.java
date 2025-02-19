package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.FriendRequestException;
import com.ttingle.chat_app_api.exceptions.NotFoundException;
import com.ttingle.chat_app_api.model.FriendRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.FriendRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserService userService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userService = userService;
    }

    @Transactional
    public void sendFriendRequest(User sender, User receiver) {
        // Check if a request already exists
        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new FriendRequestException("Friend request already sent");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequest.RequestStatus.PENDING);

        friendRequestRepository.save(request);
    }

    @Transactional
    public void respondToFriendRequest(User recipient, UUID requestId, boolean accept) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Friend request not found"));

        if(!request.getReceiver().equals(recipient)) {
            throw new FriendRequestException("You are not the recipient of this request");
        }

        if (accept) {
            request.setStatus(FriendRequest.RequestStatus.ACCEPTED);
            User sender = request.getSender();
            User receiver = request.getReceiver();
            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);
            userService.saveUser(sender);
            userService.saveUser(receiver);
        } else {
            request.setStatus(FriendRequest.RequestStatus.REJECTED);
        }
        friendRequestRepository.save(request);
    }

    public List<FriendRequest> getFriendRequestsForUser(User user) {
        return friendRequestRepository.findAllByReceiver(user);
    }
}

