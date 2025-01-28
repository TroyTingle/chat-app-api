package com.ttingle.chat_app_api.factory;

import com.ttingle.chat_app_api.model.Role;
import com.ttingle.chat_app_api.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String email, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
