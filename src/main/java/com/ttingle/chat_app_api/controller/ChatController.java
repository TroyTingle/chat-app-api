package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.chat.ChatDto;
import com.ttingle.chat_app_api.dto.chat.GroupChatRequest;
import com.ttingle.chat_app_api.dto.chat.SingleUserChatRequest;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.ChatService;
import com.ttingle.chat_app_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/1-2-1")
    public ResponseEntity<Chat> createOneToOneChat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        User participant = userService.findByUsername(singleUserChatRequest.getUsername());
        Chat chat = chatService.createOneToOneChat(requestUser, participant);
        return new ResponseEntity<>(chat, HttpStatus.CREATED);
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupChat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody GroupChatRequest groupChatRequest) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        Set<User> participantSet = Arrays.stream(groupChatRequest.getParticipants())
                .map(userService::findByUsername)
                .collect(Collectors.toSet());

        Chat chat = chatService.createGroupChat(requestUser, participantSet);
        return new ResponseEntity<>(chat, HttpStatus.CREATED);
    }

    @PostMapping("/{chatId}/participants")
    public ResponseEntity<Void> addParticipantToGroupChat(@PathVariable UUID chatId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            if(!chat.getParticipants().contains(requestUser)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            User newParticipant = userService.findByUsername(singleUserChatRequest.getUsername());

            chatService.addParticipantToGroupChat(chat, newParticipant);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{chatId}/participants")
    public ResponseEntity<Void> removeParticipantFromGroupChat(@PathVariable UUID chatId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            if(!chat.getParticipants().contains(requestUser)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            User participant = userService.findByUsername(singleUserChatRequest.getUsername());
            chatService.removeParticipantFromGroupChat(chatOptional.get(), participant);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatById(@PathVariable UUID chatId) {
        Optional<Chat> chatOptional = chatService.getById(chatId);
        return chatOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ChatDto>> getAllChatsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        List<ChatDto> chats = chatService.getAllChatsForUser(requestUser);
        return ResponseEntity.ok(chats);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId, @AuthenticationPrincipal UserDetails userDetails) {
        User requestUser = userService.findByUsername(userDetails.getUsername());
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            chatService.deleteChat(chatOptional.get(), requestUser);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}