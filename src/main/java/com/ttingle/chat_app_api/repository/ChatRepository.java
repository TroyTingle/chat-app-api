package com.ttingle.chat_app_api.repository;

import com.ttingle.chat_app_api.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
