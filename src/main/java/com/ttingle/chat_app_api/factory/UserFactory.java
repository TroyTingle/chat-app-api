package com.ttingle.chat_app_api.factory;

import com.ttingle.chat_app_api.model.Role;
import com.ttingle.chat_app_api.model.User;

import java.util.HashSet;

public class UserFactory {

    private UserFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static User createUser(String username, String email, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        user.setFriends(new HashSet<>());
        user.setReceivedRequests(new HashSet<>());
        user.setSentRequests(new HashSet<>());
        user.setChats(new HashSet<>());
        return user;
    }
}
