package com.ttingle.chat_app_api.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public ResponseEntity<Chat> createOneToOneChat(@AuthenticationPrincipal User user, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        User participant = userService.findByUsername(singleUserChatRequest.getUsername());
        Chat chat = chatService.createOneToOneChat(user, participant);
        return new ResponseEntity<>(chat, HttpStatus.CREATED);
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupChat(@AuthenticationPrincipal User user, @RequestBody GroupChatRequest groupChatRequest) {
        Set<User> participantSet = Arrays.stream(groupChatRequest.getParticipants())
                .map(userService::findByUsername)
                .collect(Collectors.toSet());

        Chat chat = chatService.createGroupChat(user, participantSet, groupChatRequest.getGroupName());
        return new ResponseEntity<>(chat, HttpStatus.CREATED);
    }

    @PostMapping("/{chatId}/participants")
    public ResponseEntity<Void> addParticipantToGroupChat(@PathVariable Long chatId, @AuthenticationPrincipal User admin, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            if(chat.getCreator() != admin) {
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
    public ResponseEntity<Void> removeParticipantFromGroupChat(@PathVariable Long chatId, @AuthenticationPrincipal User admin, @RequestBody SingleUserChatRequest singleUserChatRequest) {
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            if(chat.getCreator() != admin) {
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
    public ResponseEntity<Chat> getChatById(@PathVariable Long chatId) {
        Optional<Chat> chatOptional = chatService.getById(chatId);
        return chatOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getAllChatsForUser(@AuthenticationPrincipal User user) {
        List<Chat> chats = chatService.getAllChatsForUser(user);
        return ResponseEntity.ok(chats);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        Optional<Chat> chatOptional = chatService.getById(chatId);
        if (chatOptional.isPresent()) {
            chatService.deleteChat(chatOptional.get(), user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}