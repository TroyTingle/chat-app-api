package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.model.Message;
import com.ttingle.chat_app_api.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(Message message){
        messageRepository.save(message);
    }
}
