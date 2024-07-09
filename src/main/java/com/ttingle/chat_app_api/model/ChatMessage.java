package com.ttingle.chat_app_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChatMessage {
    @Id @GeneratedValue
    private Long messageId;
    @OneToMany @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @OneToMany @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    private String message;
    private Timestamp timestamp;
}
