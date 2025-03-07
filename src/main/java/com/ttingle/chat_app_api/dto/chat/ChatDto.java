package com.ttingle.chat_app_api.dto.chat;

import com.ttingle.chat_app_api.dto.auth.UserDto;
import com.ttingle.chat_app_api.dto.message.MessageDto;

import java.util.Set;
import java.util.UUID;

public class ChatDto {
    private UUID id;
    private String name;
    private Set<MessageDto> messages;
    private Set<UserDto> participants;

    // Constructors
    public ChatDto() {}
    public ChatDto(UUID id, String name, Set<MessageDto> messages, Set<UserDto> participants) {
        this.id = id;
        this.name = name;
        this.messages = messages;
        this.participants = participants;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<MessageDto> getMessages() {
        return messages;
    }
    public void setMessages(Set<MessageDto> messages) {
        this.messages = messages;
    }
    public Set<UserDto> getParticipants() {
        return participants;
    }
    public void setParticipants(Set<UserDto> participants) {
        this.participants = participants;
    }
}
