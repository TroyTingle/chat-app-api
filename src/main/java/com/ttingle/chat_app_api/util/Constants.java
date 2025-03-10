package com.ttingle.chat_app_api.util;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    @Value("${WEB_URL}")
    public static String WEB_URL;
}
