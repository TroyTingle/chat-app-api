package com.ttingle.chat_app_api.factory;

import com.ttingle.chat_app_api.model.Role;
import com.ttingle.chat_app_api.model.User;

public class UserFactory {

    public static User createUser(String username, String email, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
