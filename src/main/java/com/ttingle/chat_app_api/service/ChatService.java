package com.ttingle.chat_app_api.service;

import com.ttingle.chat_app_api.exceptions.ChatDeletionException;
import com.ttingle.chat_app_api.model.Chat;
import com.ttingle.chat_app_api.model.User;
import com.ttingle.chat_app_api.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        chat.setCreator(user1);
        chat.setParticipants(Set.of(user1, user2));
        return chatRepository.save(chat);
    }

    @Transactional
    public Chat createGroupChat(User creator, Set<User> participants, String name) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setCreator(creator);
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
    public Optional<Chat> getById(Long id) {
        return chatRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Chat> getAllChatsForUser(User user) {
        return chatRepository.findAll().stream()
                .filter(chat -> chat.getParticipants().contains(user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteChat(Chat chat, User user) {
        if (!chat.getCreator().equals(user)) {
            throw new ChatDeletionException("Only the creator of the chat can delete it");
        }
        chatRepository.delete(chat);
    }
}
