package com.ttingle.chat_app_api.controller;

import com.ttingle.chat_app_api.dto.chat.ChatDto;
import com.ttingle.chat_app_api.dto.chat.GroupChatRequest;
import com.ttingle.chat_app_api.dto.chat.SingleUserChatRequest;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.service.ChatService;
import com.ttingle.chat_app_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    ChatService chatService;

    @Mock
    UserService userService;

    @InjectMocks
    ChatController chatController;

    @Test
    void testCreateOneToOneChat_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        User participant = new User();
        participant.setUsername("user2");

        Chat chat = new Chat();

        when(userService.findByUsername(anyString())).thenReturn(participant);
        when(chatService.createOneToOneChat(any(User.class), any(User.class))).thenReturn(chat);

        ResponseEntity<Chat> response = chatController.createOneToOneChat(user, singleUserChatRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(chat, response.getBody());
    }

    @Test
    void testCreateGroupChat_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User requestUser = new User();

        User participant1 = new User();
        participant1.setUsername("user2");

        User participant2 = new User();
        participant2.setUsername("user3");

        GroupChatRequest groupChatRequest = new GroupChatRequest();
        groupChatRequest.setParticipants(new String[]{"user2", "user3"});
        groupChatRequest.setGroupName("Group Chat");

        Chat chat = new Chat();

        when(userService.findByUsername("user1")).thenReturn(requestUser);
        when(userService.findByUsername("user2")).thenReturn(participant1);
        when(userService.findByUsername("user3")).thenReturn(participant2);
        when(chatService.createGroupChat(eq(requestUser), any(Set.class))).thenReturn(chat);

        ResponseEntity<Chat> response = chatController.createGroupChat(user, groupChatRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(chat, response.getBody());
    }

    @Test
    void testAddParticipantToGroupChat_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User requestUser = new User();
        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setParticipants(Set.of(requestUser));

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(userService.findByUsername(user.getUsername())).thenReturn(requestUser);
        when(userService.findByUsername(singleUserChatRequest.getUsername())).thenReturn(new User());

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testAddParticipantToGroupChat_ChatNotFound() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testAddParticipantToGroupChat_Forbidden() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setParticipants(new HashSet<>());

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(chatService, never()).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testRemoveParticipantFromGroupChat_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User admin = new User();

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setParticipants(Set.of(admin));

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(userService.findByUsername(user.getUsername())).thenReturn(admin);
        when(userService.findByUsername(singleUserChatRequest.getUsername())).thenReturn(new User());

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testRemoveParticipantFromGroupChat_ChatNotFound() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testRemoveParticipantFromGroupChat_Forbidden() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        Chat chat = new Chat();
        chat.setParticipants(new HashSet<>());

        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(UUID.randomUUID(), user, singleUserChatRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(chatService, never()).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    void testGetChatById_Success() {
        Chat chat = new Chat();
        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));

        ResponseEntity<Chat> response = chatController.getChatById(UUID.randomUUID());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chat, response.getBody());
    }

    @Test
    void testGetChatById_NotFound() {
        when(chatService.getById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseEntity<Chat> response = chatController.getChatById(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllChatsForUser_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        User requestUser = new User();
        List<ChatDto> chats = List.of(new ChatDto(), new ChatDto());

        when(userService.findByUsername(user.getUsername())).thenReturn(requestUser);
        when(chatService.getAllChatsForUser(requestUser)).thenReturn(chats);

        ResponseEntity<List<ChatDto>> response = chatController.getAllChatsForUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chats, response.getBody());
    }

    @Test
    void testDeleteChat_Success() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));

        User requestUser = new User();

        Chat chat = new Chat();
        when(chatService.getById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(userService.findByUsername(user.getUsername())).thenReturn(requestUser);

        ResponseEntity<Void> response = chatController.deleteChat(UUID.randomUUID(), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).deleteChat(chat, requestUser);
    }

    @Test
    void testDeleteChat_NotFound() {
        UserDetails user = new org.springframework.security.core.userdetails.User("user1", "password", Set.of((GrantedAuthority) () -> "ROLE_USER"));
        when(chatService.getById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.deleteChat(UUID.randomUUID(), user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).deleteChat(any(Chat.class), any(User.class));
    }
}