package services;

import models.Message;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Repository for message storage and retrieval with conversation grouping
 */
public class MessageRepository {

    private final repositories.MessageRepository jpaMessageRepository;

    public MessageRepository(repositories.MessageRepository jpaMessageRepository) {
        this.jpaMessageRepository = jpaMessageRepository;
    }

    public Message saveMessage(Long senderId, Long recipientId, Long propertyId, String text, boolean isBlocked) {
        Message message = new Message(null, senderId, recipientId, propertyId, text);
        message.setBlocked(isBlocked);
        return jpaMessageRepository.save(message);
    }

    // Overload for backward compatibility if needed, though we should update
    // callers
    public Message saveMessage(Long senderId, Long recipientId, Long propertyId, String text) {
        return saveMessage(senderId, recipientId, propertyId, text, false);
    }

    public List<Message> getMessagesForUser(Long userId) {
        return jpaMessageRepository.findByRecipientId(userId);
    }

    public List<Message> getConversation(Long user1Id, Long user2Id) {
        List<Message> sent = jpaMessageRepository.findBySenderId(user1Id).stream()
                .filter(m -> m.getRecipientId().equals(user2Id))
                .collect(Collectors.toList());

        List<Message> received = jpaMessageRepository.findBySenderId(user2Id).stream()
                .filter(m -> m.getRecipientId().equals(user1Id))
                .filter(m -> !m.isBlocked()) // Filter out blocked messages for recipient
                .collect(Collectors.toList());

        return Stream.concat(sent.stream(), received.stream())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesByProperty(Long propertyId) {
        return jpaMessageRepository.findByPropertyId(propertyId);
    }

    public Map<Long, List<Message>> getConversationsByProperty(Long userId) {
        Map<Long, List<Message>> grouped = new HashMap<>();

        // Get all messages where user is sender or recipient
        List<Message> inbox = jpaMessageRepository.findByRecipientId(userId);
        List<Message> outbox = jpaMessageRepository.findBySenderId(userId);

        List<Message> all = Stream.concat(inbox.stream(), outbox.stream())
                .distinct()
                .collect(Collectors.toList());

        for (Message msg : all) {
            Long propId = msg.getPropertyId() != null ? msg.getPropertyId() : 0L;
            grouped.computeIfAbsent(propId, k -> new ArrayList<>()).add(msg);
        }

        return grouped;
    }

    public void markAsRead(Long messageId) {
        Optional<Message> msgOpt = jpaMessageRepository.findById(messageId);
        msgOpt.ifPresent(m -> {
            m.setRead(true);
            jpaMessageRepository.save(m);
        });
    }

    public void markConversationAsRead(Long userId, Long otherUserId) {
        // Find messages where recipient is userId AND sender is otherUserId
        // This logic is slightly inefficient without a custom query but works for
        // migration
        List<Message> conversation = jpaMessageRepository.findBySenderId(otherUserId).stream()
                .filter(m -> m.getRecipientId().equals(userId))
                .collect(Collectors.toList());

        for (Message m : conversation) {
            m.setRead(true);
            jpaMessageRepository.save(m);
        }
    }

    public int getUnreadCount(Long userId) {
        return (int) jpaMessageRepository.findByRecipientId(userId).stream()
                .filter(m -> !m.isRead())
                .count();
    }

    public boolean hasUserContactedSeller(Long buyerId, Long sellerId) {
        return jpaMessageRepository.existsBySenderIdAndRecipientId(buyerId, sellerId);
    }

    public List<Message> getMessagesSentByUser(Long userId) {
        return jpaMessageRepository.findBySenderId(userId);
    }

    public void deleteMessage(Long messageId) {
        jpaMessageRepository.deleteById(messageId);
    }

    public List<Message> getAllMessages() {
        return jpaMessageRepository.findAll();
    }

    public List<Message> searchMessages(Long userId, String query) {
        return jpaMessageRepository.searchMessages(userId, query);
    }

    public void deleteConversation(Long userId, Long otherUserId) {
        jpaMessageRepository.deleteConversation(userId, otherUserId);
    }
}
