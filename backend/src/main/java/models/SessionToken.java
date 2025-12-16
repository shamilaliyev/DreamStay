package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user session token for secure authentication (NFR-03)
 */
public class SessionToken {
    private String token;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;

    public SessionToken(Long userId, int expiryMinutes) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiryTime = this.createdAt.plusMinutes(expiryMinutes);
    }

    public SessionToken(String token, Long userId, LocalDateTime createdAt, LocalDateTime expiryTime) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public String toString() {
        return "SessionToken{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", expiryTime=" + expiryTime +
                ", expired=" + isExpired() +
                '}';
    }
}
