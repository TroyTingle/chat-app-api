package com.ttingle.chat_app_api.repository;

import com.ttingle.chat_app_api.model.FriendRequest;
import com.ttingle.chat_app_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    boolean existsBySenderAndReceiver(User sender, User receiver);
    List<FriendRequest> findAllByReceiver(User user);
}
