package services;

import models.Block;
import models.Message;
import models.User;
import repositories.BlockRepository;

import java.util.List;
import java.util.stream.Collectors;

public class MailService {
    private MessageRepository messageRepository;
    private AuthService authService;
    private BlockRepository blockRepository;

    public MailService(AuthService authService, MessageRepository messageRepository, BlockRepository blockRepository) {
        this.authService = authService;
        this.messageRepository = messageRepository;
        this.blockRepository = blockRepository;
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

        boolean blocked = false;
        if (isBlocked(recipient.getId(), sender.getId())) {
            System.out
                    .println("Message silently blocked: " + sender.getName() + " is blocked by " + recipient.getName());
            blocked = true;
        }

        messageRepository.saveMessage(sender.getId(), recipient.getId(), null, text, blocked);
        System.out.println("Message sent (blocked=" + blocked + ") to " + recipient.getName());
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

    // Blocking Logic

    public void blockUser(Long blockerId, Long blockedId) {
        if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            blockRepository.save(new Block(blockerId, blockedId));
        }
    }

    public void unblockUser(Long blockerId, Long blockedId) {
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    public boolean isBlocked(Long blockerId, Long blockedId) {
        return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    // New Features

    public List<Message> searchMessages(Long userId, String query) {
        return messageRepository.searchMessages(userId, query);
    }

    public void deleteConversation(Long userId, Long otherUserId) {
        messageRepository.deleteConversation(userId, otherUserId);
    }
}
