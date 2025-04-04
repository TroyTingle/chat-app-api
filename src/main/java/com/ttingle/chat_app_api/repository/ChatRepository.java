package com.ttingle.chat_app_api.repository;

import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    List<Chat> findAllByParticipantsContaining(User user);
}
