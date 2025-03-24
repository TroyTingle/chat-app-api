package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.auth.LoginRequest;
import com.ttingle.chat_app_api.dto.auth.SignupRequest;
import com.ttingle.chat_app_api.dto.auth.UpdatePasswordRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.UserService;
import com.ttingle.chat_app_api.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthController userAuthController;


    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("token", "token");
        expectedResponse.put("type", "Bearer");

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("token");

        ResponseEntity<?> response = userAuthController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testLogin_Failure() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<?> response = userAuthController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testSignup_Success() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");

        when(userService.existsByUsername(anyString())).thenReturn(false);

        ResponseEntity<?> response = userAuthController.signup(signupRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Signup Successful!", response.getBody());
    }

    @Test
    void testSignup_UsernameExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("existinguser@example.com");
        signupRequest.setPassword("password");

        when(userService.existsByUsername(anyString())).thenReturn(true);

        ResponseEntity<?> response = userAuthController.signup(signupRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testUpdatePassword_Success() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("oldpassword");
        updatePasswordRequest.setNewPassword("newpassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedOldPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        ResponseEntity<String> response = userAuthController.updatePassword("testuser", updatePasswordRequest, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully", response.getBody());
    }

    @Test
    void testUpdatePassword_Unauthorized() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("oldpassword");
        updatePasswordRequest.setNewPassword("newpassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("differentuser");

        ResponseEntity<String> response = userAuthController.updatePassword("testuser", updatePasswordRequest, userDetails);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized Request", response.getBody());
    }

    @Test
    void testUpdatePassword_OldPasswordMismatch() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("wrongoldpassword");
        updatePasswordRequest.setNewPassword("newpassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedOldPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<String> response = userAuthController.updatePassword("testuser", updatePasswordRequest, userDetails);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Old password does not match", response.getBody());
    }

    @Test
    void testLogin_EmptyUsernameAndPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<?> response = userAuthController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdatePassword_SameOldAndNewPasswords() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("samepassword");
        updatePasswordRequest.setNewPassword("samepassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedSamePassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ResponseEntity<String> response = userAuthController.updatePassword("testuser", updatePasswordRequest, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("New password cannot be the same as the old password", response.getBody());
    }
}