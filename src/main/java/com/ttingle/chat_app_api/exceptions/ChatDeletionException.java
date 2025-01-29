package com.ttingle.chat_app_api.exceptions;

public class ChatDeletionException extends RuntimeException {
    public ChatDeletionException(String message) {
        super(message);
    }
}
