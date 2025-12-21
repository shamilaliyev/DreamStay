package models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long recipientId;
    private Long propertyId; // Optional, can be null
    private String text;
    private LocalDateTime timestamp;
    private boolean isRead;
    private Boolean isBlocked = false; // changed to Boolean to allow nulls during migration

    protected Message() {
        // JPA requires no-arg constructor
    }

    public Message(Long id, Long senderId, Long recipientId, Long propertyId, String text) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.propertyId = propertyId;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.isBlocked = false;
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

    public Boolean isBlocked() {
        return isBlocked != null && isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
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
                ", blocked=" + isBlocked +
                ", text='" + text + '\'' +
                '}';
    }
}
