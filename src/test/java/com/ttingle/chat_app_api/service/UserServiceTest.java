package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.dto.auth.SignupRequest;
import com.ttingle.chat_app_api.exceptions.UserNotFoundException;
import com.ttingle.chat_app_api.factory.UserFactory;
import com.ttingle.chat_app_api.model.Role;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindByUsername_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    public void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("nonexistentuser"));
    }

    @Test
    public void testExistsByUsername_UserExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        boolean exists = userService.existsByUsername("testuser");

        assertTrue(exists);
    }

    @Test
    public void testExistsByUsername_UserDoesNotExist() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        boolean exists = userService.existsByUsername("nonexistentuser");

        assertFalse(exists);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testuser");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));
    }

    @Test
    public void testCreateUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");

        User user = new User();
        user.setUsername("newuser");
        when(userFactory.createUser(anyString(), anyString(), anyString())).thenReturn(user);

        userService.createUser(signupRequest);

        verify(userFactory, times(1)).createUser("newuser", "newuser@example.com", "password");
        verify(userRepository, times(1)).save(user);
    }
}