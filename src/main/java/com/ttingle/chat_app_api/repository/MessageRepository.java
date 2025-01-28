package com.ttingle.chat_app_api.repository;

import com.ttingle.chat_app_api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
