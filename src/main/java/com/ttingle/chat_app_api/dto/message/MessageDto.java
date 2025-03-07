package com.ttingle.chat_app_api.dto.message;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDto {
    private UUID id;
    private String content;
    private LocalDateTime timestamp;
    private String senderUsername;
    private UUID chatId;

    // Constructors
    public MessageDto(UUID id, String content, LocalDateTime timestamp, String senderUsername, UUID chatId) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.senderUsername = senderUsername;
        this.chatId = chatId;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
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
    public UUID getChatId() {
        return chatId;
    }
    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }
}
