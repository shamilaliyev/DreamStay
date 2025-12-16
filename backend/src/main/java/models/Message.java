package models;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private Long propertyId; // Optional, can be null
    private String text;
    private LocalDateTime timestamp;
    private boolean isRead;

    public Message(Long id, Long senderId, Long recipientId, Long propertyId, String text) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.propertyId = propertyId;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + senderId +
                ", recipient=" + recipientId +
                ", prop=" + propertyId +
                ", time=" + timestamp +
                ", read=" + isRead +
                ", text='" + text + '\'' +
                '}';
    }
}
