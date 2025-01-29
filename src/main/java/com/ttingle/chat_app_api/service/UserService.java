package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.UserNotFoundException;
import com.ttingle.chat_app_api.factory.UserFactory;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User findByUsername (String username) throws UserNotFoundException{
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void saveUser(User user){
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
        List<GrantedAuthority> authorities =  new ArrayList<>(Collections.emptyList());
        authorities.add((GrantedAuthority) () -> user.getRole().name());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Transactional
    public void createUser(String username, String email, String password){
        User newUser = UserFactory.createUser(username, email, password);
        this.saveUser(newUser);
    }
}
