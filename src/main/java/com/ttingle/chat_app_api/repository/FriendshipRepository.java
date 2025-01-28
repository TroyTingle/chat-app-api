package com.ttingle.chat_app_api.repository;

import com.ttingle.chat_app_api.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
