package com.ttingle.chat_app_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChatMessage {
    private User sender;
    private User recipient;
    private String message;
}
