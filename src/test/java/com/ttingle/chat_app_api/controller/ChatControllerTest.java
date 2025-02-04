package com.ttingle.chat_app_api.controller;

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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    ChatService chatService;

    @Mock
    UserService userService;

    @InjectMocks
    ChatController chatController;

    @Test
    public void testCreateOneToOneChat_Success() {
        User user = new User();
        user.setUsername("user1");

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
    public void testCreateGroupChat_Success() {
        User user = new User();
        user.setUsername("user1");

        User participant1 = new User();
        participant1.setUsername("user2");

        User participant2 = new User();
        participant2.setUsername("user3");

        GroupChatRequest groupChatRequest = new GroupChatRequest();
        groupChatRequest.setParticipants(new String[]{"user2", "user3"});
        groupChatRequest.setGroupName("Group Chat");

        Chat chat = new Chat();

        when(userService.findByUsername("user2")).thenReturn(participant1);
        when(userService.findByUsername("user3")).thenReturn(participant2);
        when(chatService.createGroupChat(any(User.class), any(Set.class), anyString())).thenReturn(chat);

        ResponseEntity<Chat> response = chatController.createGroupChat(user, groupChatRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(chat, response.getBody());
    }

    @Test
    public void testAddParticipantToGroupChat_Success() {
        User admin = new User();
        admin.setUsername("admin");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setCreator(admin);

        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));
        when(userService.findByUsername(anyString())).thenReturn(new User());

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testAddParticipantToGroupChat_ChatNotFound() {
        User admin = new User();
        admin.setUsername("admin");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        when(chatService.getById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testAddParticipantToGroupChat_Forbidden() {
        User admin = new User();
        admin.setUsername("admin");

        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setCreator(anotherUser);

        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));

        ResponseEntity<Void> response = chatController.addParticipantToGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(chatService, never()).addParticipantToGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testRemoveParticipantFromGroupChat_Success() {
        User admin = new User();
        admin.setUsername("admin");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        Chat chat = new Chat();
        chat.setCreator(admin);

        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));
        when(userService.findByUsername(anyString())).thenReturn(new User());

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testRemoveParticipantFromGroupChat_ChatNotFound() {
        User admin = new User();
        admin.setUsername("admin");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        when(chatService.getById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testRemoveParticipantFromGroupChat_Forbidden() {
        User admin = new User();
        admin.setUsername("admin");

        SingleUserChatRequest singleUserChatRequest = new SingleUserChatRequest();
        singleUserChatRequest.setUsername("user2");

        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        Chat chat = new Chat();
        chat.setCreator(anotherUser);

        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));

        ResponseEntity<Void> response = chatController.removeParticipantFromGroupChat(1L, admin, singleUserChatRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(chatService, never()).removeParticipantFromGroupChat(any(Chat.class), any(User.class));
    }

    @Test
    public void testGetChatById_Success() {
        Chat chat = new Chat();
        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));

        ResponseEntity<Chat> response = chatController.getChatById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chat, response.getBody());
    }

    @Test
    public void testGetChatById_NotFound() {
        when(chatService.getById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Chat> response = chatController.getChatById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllChatsForUser_Success() {
        User user = new User();
        List<Chat> chats = List.of(new Chat(), new Chat());
        when(chatService.getAllChatsForUser(user)).thenReturn(chats);

        ResponseEntity<List<Chat>> response = chatController.getAllChatsForUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chats, response.getBody());
    }

    @Test
    public void testDeleteChat_Success() {
        User user = new User();
        Chat chat = new Chat();
        when(chatService.getById(anyLong())).thenReturn(Optional.of(chat));

        ResponseEntity<Void> response = chatController.deleteChat(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService, times(1)).deleteChat(chat, user);
    }

    @Test
    public void testDeleteChat_NotFound() {
        User user = new User();
        when(chatService.getById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = chatController.deleteChat(1L, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(chatService, never()).deleteChat(any(Chat.class), any(User.class));
    }
}