package com.ttingle.chat_app_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ChatAppApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatAppApiApplication.class, args);
	}

}
