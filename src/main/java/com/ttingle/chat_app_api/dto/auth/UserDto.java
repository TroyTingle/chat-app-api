package com.ttingle.chat_app_api.dto.auth;

public class UserDto {
    private String username;

    public UserDto(String username) {
        this.username = username;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
