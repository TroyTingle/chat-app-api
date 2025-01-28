package com.ttingle.chat_app_api.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("No user with username: " + username);
    }
}
