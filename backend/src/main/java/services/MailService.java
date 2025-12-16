package services;

import models.Message;
import models.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MailService {
    private static final String FILE_PATH = "messages.json";
    private MessageRepository messageRepository;
    private AuthService authService;

    public MailService(AuthService authService, MessageRepository messageRepository) {
        this.authService = authService;
        this.messageRepository = messageRepository;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void sendMessage(User sender, String recipientEmail, String text) {
        User recipient = authService.getUserByEmail(recipientEmail);
        if (recipient != null) {
            sendMessage(sender, recipient, text);
        } else {
            System.out.println("Recipient not found: " + recipientEmail);
        }
    }

    public void sendMessage(User sender, User recipient, String text) {
        if (recipient == null) {
            System.out.println("Recipient is null, cannot send message.");
            return;
        }
        messageRepository.saveMessage(sender.getId(), recipient.getId(), null, text);
        System.out.println("Message sent to " + recipient.getName());
    }

    public List<Message> getInbox(Long userId) {
        return messageRepository.getMessagesForUser(userId);
    }

    public List<Message> getChatHistory(Long userId, Long otherUserId) {
        return messageRepository.getConversation(userId, otherUserId);
    }

    // Returns list of Users that the current user has a chat history with
    public List<User> getChatPartners(Long userId) {
        List<Message> all = messageRepository.getAllMessages();
        return all.stream()
                .filter(m -> m.getSenderId().equals(userId) || m.getRecipientId().equals(userId))
                .map(m -> m.getSenderId().equals(userId) ? m.getRecipientId() : m.getSenderId())
                .distinct()
                .map(id -> authService.getUserById(id))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }
}
