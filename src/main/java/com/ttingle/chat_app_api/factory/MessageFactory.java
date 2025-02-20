package com.ttingle.chat_app_api.factory;

import com.ttingle.chat_app_api.dto.message.ChatMessage;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.Message;
import com.ttingle.chat_app_api.model.User;

import java.time.LocalDateTime;

public class MessageFactory {

    private MessageFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Message createMessage(User sender, Chat chat, ChatMessage chatMessage){
        Message message = new Message();
        message.setContent(chatMessage.getContent());
        message.setSender(sender);
        message.setChat(chat);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

}
