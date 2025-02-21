package com.ttingle.chat_app_api.dto.message;

import java.time.LocalDateTime;

public class ChatMessage {
    private String content;
    private LocalDateTime timestamp;
    private String senderUsername;
    private String chatId;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String content, LocalDateTime timestamp,
                       String senderUsername, String chatId) {
        this.content = content;
        this.timestamp = timestamp;
        this.senderUsername = senderUsername;
        this.chatId = chatId;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
