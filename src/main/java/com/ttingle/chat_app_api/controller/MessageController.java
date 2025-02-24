package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.message.ChatMessage;
import com.ttingle.chat_app_api.factory.MessageFactory;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.Message;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.ChatService;
import com.ttingle.chat_app_api.service.MessageService;
import com.ttingle.chat_app_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class MessageController {

    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    public MessageController(ChatService chatService, UserService userService,
                             MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat/{chatId}")
    public void sendMessage(@DestinationVariable String chatId, @Payload ChatMessage chatMessage) {
        Optional<Chat> chat = chatService.getById(UUID.fromString(chatId));

        if(chat.isPresent()){
            // save the message to the database
            User sender = userService.findByUsername(chatMessage.getSenderUsername());
            Message message = MessageFactory.createMessage(sender, chat.get(), chatMessage);
            messageService.saveMessage(message);

            // Broadcast the message to all participants in the chat
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, chatMessage);
        }
    }

    public ChatMessage[] getMessagesForChat(UUID chatId) {
        return messageService.getMessagesForChat(chatId);
    }

}
