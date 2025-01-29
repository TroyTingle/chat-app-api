package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.auth.LoginRequest;
import com.ttingle.chat_app_api.dto.auth.SignupRequest;
import com.ttingle.chat_app_api.dto.auth.UpdatePasswordRequest;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.UserService;
import com.ttingle.chat_app_api.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public UserAuthController(AuthenticationManager authenticationManager, UserService userService,JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PutMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest){
        //Check if username already exists
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        //Create new user
        userService.createUser(signupRequest.getUsername(), signupRequest.getEmail(),passwordEncoder.encode(signupRequest.getPassword()));
        return ResponseEntity.ok("Signup Successful!");
    }

    @PutMapping("/{username}/update-password")
    public ResponseEntity<String> updatePassword(@PathVariable String username,
                                                 @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, @AuthenticationPrincipal UserDetails userDetails) {

        //Checks the request user and supplied username are the same
        if (!username.equals(userDetails.getUsername())) {
            return new ResponseEntity<>("Unauthorized Request", HttpStatus.FORBIDDEN);
        }

        User user = userService.findByUsername(username);

        //Check old password matches the one in the database
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())){
            return new ResponseEntity<>("Old password does not match", HttpStatus.FORBIDDEN);
        }

        //Check new password is not the same as the old password
        if (passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())){
            return new ResponseEntity<>("New password cannot be the same as the old password", HttpStatus.BAD_REQUEST);
        }

        //Save new password to the database
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userService.saveUser(user);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }
}
