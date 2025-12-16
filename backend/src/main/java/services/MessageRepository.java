package services;

import models.Message;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for message storage and retrieval with conversation grouping
 * (FR-07)
 * Provides structured access to messages and tracks user interactions
 */
public class MessageRepository {
    private static final String FILE_PATH = "messages.json";
    private List<Message> messages;
    private long nextId;

    public MessageRepository() {
        this.messages = new ArrayList<>();
        this.nextId = 1;
        loadMessages();
    }

    /**
     * Save a new message
     */
    public Message saveMessage(Long senderId, Long recipientId, Long propertyId, String text) {
        Message message = new Message(nextId++, senderId, recipientId, propertyId, text);
        messages.add(message);
        saveMessages();
        return message;
    }

    /**
     * Get all messages for a user (inbox)
     */
    public List<Message> getMessagesForUser(Long userId) {
        return messages.stream()
                .filter(m -> m.getRecipientId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Get conversation between two users
     */
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messages.stream()
                .filter(m -> (m.getSenderId().equals(user1Id) && m.getRecipientId().equals(user2Id)) ||
                        (m.getSenderId().equals(user2Id) && m.getRecipientId().equals(user1Id)))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Get messages about a specific property
     */
    public List<Message> getMessagesByProperty(Long propertyId) {
        return messages.stream()
                .filter(m -> propertyId.equals(m.getPropertyId()))
                .collect(Collectors.toList());
    }

    /**
     * Get conversations grouped by property for a user
     */
    public Map<Long, List<Message>> getConversationsByProperty(Long userId) {
        Map<Long, List<Message>> grouped = new HashMap<>();

        List<Message> userMessages = messages.stream()
                .filter(m -> m.getRecipientId().equals(userId) || m.getSenderId().equals(userId))
                .collect(Collectors.toList());

        for (Message msg : userMessages) {
            Long propId = msg.getPropertyId() != null ? msg.getPropertyId() : 0L;
            grouped.computeIfAbsent(propId, k -> new ArrayList<>()).add(msg);
        }

        return grouped;
    }

    /**
     * Mark a message as read
     */
    public void markAsRead(Long messageId) {
        messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .ifPresent(m -> {
                    m.setRead(true);
                    saveMessages();
                });
    }

    /**
     * Mark all messages in a conversation as read
     */
    public void markConversationAsRead(Long userId, Long otherUserId) {
        messages.stream()
                .filter(m -> m.getRecipientId().equals(userId) && m.getSenderId().equals(otherUserId))
                .forEach(m -> m.setRead(true));
        saveMessages();
    }

    /**
     * Get unread message count for a user
     */
    public int getUnreadCount(Long userId) {
        return (int) messages.stream()
                .filter(m -> m.getRecipientId().equals(userId) && !m.isRead())
                .count();
    }

    /**
     * Check if a user has contacted another user (for review validation)
     */
    public boolean hasUserContactedSeller(Long buyerId, Long sellerId) {
        return messages.stream()
                .anyMatch(m -> m.getSenderId().equals(buyerId) && m.getRecipientId().equals(sellerId));
    }

    /**
     * Get all messages sent by a user
     */
    public List<Message> getMessagesSentByUser(Long userId) {
        return messages.stream()
                .filter(m -> m.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Delete a message
     */
    public void deleteMessage(Long messageId) {
        messages.removeIf(m -> m.getId().equals(messageId));
        saveMessages();
    }

    // Persistence methods
    private void loadMessages() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            parseJson(sb.toString());
        } catch (IOException e) {
            System.err.println("Error loading messages: " + e.getMessage());
        }
    }

    private void saveMessages() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(toJson());
        } catch (IOException e) {
            System.err.println("Error saving messages: " + e.getMessage());
        }
    }

    private void parseJson(String json) {
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return;
        }

        String inner = json.trim();
        if (inner.startsWith("["))
            inner = inner.substring(1);
        if (inner.endsWith("]"))
            inner = inner.substring(0, inner.length() - 1);

        List<String> msgStrings = splitJsonObjects(inner);
        messages.clear();
        for (String s : msgStrings) {
            Message m = parseMessage(s);
            if (m != null) {
                messages.add(m);
                if (m.getId() >= nextId)
                    nextId = m.getId() + 1;
            }
        }
    }

    private List<String> splitJsonObjects(String inner) {
        List<String> list = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        for (char c : inner.toCharArray()) {
            if (c == '{')
                braceCount++;
            if (c == '}')
                braceCount--;
            current.append(c);
            if (braceCount == 0 && c == '}' && current.length() > 0) {
                list.add(current.toString());
                current = new StringBuilder();
            } else if (braceCount == 0 && c == ',') {
                current = new StringBuilder();
            }
        }
        return list;
    }

    private Message parseMessage(String json) {
        Map<String, String> fields = new HashMap<>();
        String content = json.trim();
        if (content.startsWith("{"))
            content = content.substring(1);
        if (content.endsWith("}"))
            content = content.substring(0, content.length() - 1);

        // Simple manual split - prone to error if text contains commas.
        // Using a slightly more robust split
        String[] pairs = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length == 2) {
                fields.put(kv[0].trim().replace("\"", ""), kv[1].trim().replace("\"", ""));
            }
        }

        try {
            Long id = Long.parseLong(fields.get("id"));
            Long senderId = Long.parseLong(fields.get("senderId"));
            Long recipientId = Long.parseLong(fields.get("recipientId"));
            Long propId = fields.containsKey("propertyId") && !fields.get("propertyId").equals("null")
                    ? Long.parseLong(fields.get("propertyId"))
                    : null;
            String text = fields.getOrDefault("text", "");
            boolean isRead = Boolean.parseBoolean(fields.getOrDefault("isRead", "false"));
            Message m = new Message(id, senderId, recipientId, propId, text);
            m.setRead(isRead);
            // Timestamp restore ignored for simplicity or set to now
            return m;
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            sb.append(String.format(
                    "{\"id\":%d,\"senderId\":%d,\"recipientId\":%d,\"propertyId\":%s,\"text\":\"%s\",\"isRead\":%b}",
                    m.getId(), m.getSenderId(), m.getRecipientId(),
                    m.getPropertyId() == null ? "null" : m.getPropertyId(),
                    m.getText().replace("\"", "'"), // Escape quotes simply
                    m.isRead()));
            if (i < messages.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Get all messages
     */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
