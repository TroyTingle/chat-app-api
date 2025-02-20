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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
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

    @MessageMapping("/{chatId}")
    public void sendMessage(@AuthenticationPrincipal UserDetails userDetails, @DestinationVariable UUID chatId, @Payload ChatMessage chatMessage) {
        Optional<Chat> chat = chatService.getById(chatId);

        if(chat.isPresent()){
            // save the message to the database
            User sender = userService.findByUsername(userDetails.getUsername());
            Message message = MessageFactory.createMessage(sender, chat.get(), chatMessage);
            messageService.saveMessage(message);

            // Broadcast the message to all participants in the chat
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
        }
    }

}
