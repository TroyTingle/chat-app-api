package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.ChatDeletionException;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
   void testCreateOneToOneChat() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Chat chat = new Chat();
        chat.setName("user1 & user2");
        chat.setParticipants(Set.of(user1, user2));

        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        Chat createdChat = chatService.createOneToOneChat(user1, user2);

        assertNotNull(createdChat);
        assertEquals("user1 & user2", createdChat.getName());
        assertTrue(createdChat.getParticipants().contains(user1));
        assertTrue(createdChat.getParticipants().contains(user2));
    }

    @Test
    void testCreateGroupChat() {
        User creator = new User();
        creator.setUsername("creator");
        Set<User> participants = new HashSet<>();
        User participant = new User();
        participant.setUsername("participant");
        participants.add(participant);

        Chat chat = new Chat();
        chat.setName("Group Chat");
        chat.setParticipants(participants);
        chat.getParticipants().add(creator);

        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        Chat createdChat = chatService.createGroupChat(creator, participants);

        assertNotNull(createdChat);
        assertEquals("Group Chat", createdChat.getName());
        assertTrue(createdChat.getParticipants().contains(creator));
        assertTrue(createdChat.getParticipants().contains(participant));
    }

    @Test
    void testAddParticipantToGroupChat() {
        Chat chat = new Chat();
        chat.setParticipants(new HashSet<>());
        User participant = new User();
        participant.setUsername("participant");

        chatService.addParticipantToGroupChat(chat, participant);

        assertTrue(chat.getParticipants().contains(participant));
        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testRemoveParticipantFromGroupChat() {
        Chat chat = new Chat();
        chat.setParticipants(new HashSet<>());
        User participant = new User();
        participant.setUsername("participant");
        chat.getParticipants().add(participant);

        chatService.removeParticipantFromGroupChat(chat, participant);

        assertFalse(chat.getParticipants().contains(participant));
        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testGetById() {
        Chat chat = new Chat();
        UUID id = UUID.randomUUID();
        chat.setId(id);
        when(chatRepository.findById(id)).thenReturn(Optional.of(chat));

        Optional<Chat> foundChat = chatService.getById(id);

        assertTrue(foundChat.isPresent());
        assertEquals(id, foundChat.get().getId());
    }

    @Test
    void testDeleteChat_Participant() {
        User user = new User();
        Chat chat = new Chat();
        chat.setParticipants(Set.of(user));

        chatService.deleteChat(chat, user);

        verify(chatRepository, times(1)).delete(chat);
    }

    @Test
    void testDeleteChat_NotParticipant() {
        User user = new User();
        Chat chat = new Chat();
        chat.setParticipants(new HashSet<>());

        assertThrows(ChatDeletionException.class, () -> chatService.deleteChat(chat, user));
        verify(chatRepository, never()).delete(chat);
    }
}
