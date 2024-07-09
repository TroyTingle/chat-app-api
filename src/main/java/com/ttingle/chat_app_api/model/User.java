package com.ttingle.chat_app_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    private UUID userId;
    private String userName;
}