package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.ChatDeletionException;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Chat createOneToOneChat(User user1, User user2) {
        Chat chat = new Chat();
        chat.setName(user1.getUsername() + " & " + user2.getUsername());
        chat.setParticipants(Set.of(user1, user2));
        chat.setMessages(new HashSet<>());
        return chatRepository.save(chat);
    }

    @Transactional
    public Chat createGroupChat(User creator, Set<User> participants) {
        Chat chat = new Chat();
        chat.setName(creator.getUsername() + participants.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", ")));
        chat.setParticipants(participants);
        chat.getParticipants().add(creator);
        return chatRepository.save(chat);
    }

    @Transactional
    public void addParticipantToGroupChat(Chat chat, User participant) {
        chat.getParticipants().add(participant);
        chatRepository.save(chat);
    }

    @Transactional
    public void removeParticipantFromGroupChat(Chat chat, User participant) {
        chat.getParticipants().remove(participant);
        chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public Optional<Chat> getById(UUID id) {
        return chatRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Chat> getAllChatsForUser(User user) {
        return chatRepository.findAll().stream()
                .filter(chat -> chat.getParticipants().contains(user))
                .toList();
    }

    @Transactional
    public void deleteChat(Chat chat, User user) {
        if (!chat.getParticipants().contains(user)) {
            throw new ChatDeletionException("User is not a participant of the chat");
        }
        chatRepository.delete(chat);
    }
}
