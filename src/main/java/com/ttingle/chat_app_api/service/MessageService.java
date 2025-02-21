package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.dto.message.ChatMessage;
import com.ttingle.chat_app_api.model.Message;
import com.ttingle.chat_app_api.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(Message message){
        messageRepository.save(message);
    }

    public ChatMessage[] getMessagesForChat(UUID chatId) {
        return Arrays.stream(messageRepository.getMessagesByChatId(chatId))
                .map(message -> new ChatMessage(message.getContent(), message.getTimestamp(), message.getSender().getUsername(), message.getChat().getId().toString()))
                .toArray(ChatMessage[]::new);
    }
}
