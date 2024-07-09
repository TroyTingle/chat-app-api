package com.ttingle.chat_app_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FriendRequest {
    @Id @GeneratedValue
    private Long id;
    private Long senderId;
    private Long recipientId;
}
