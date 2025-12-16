package services;

import models.SessionToken;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Security service for password hashing, validation, and session management
 * (NFR-03)
 * Uses SHA-256 with salt for password hashing (BCrypt alternative for
 * simplicity)
 */
public class SecurityService {
    private static final int SALT_LENGTH = 16;
    private static final int SESSION_EXPIRY_MINUTES = 120; // 2 hours
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");

    private Map<String, SessionToken> activeSessions;
    private SecureRandom random;

    public SecurityService() {
        this.activeSessions = new HashMap<>();
        this.random = new SecureRandom();
    }

    /**
     * Hash a password with a random salt using SHA-256
     * Format: salt:hash
     */
    public String hashPassword(String password) {
        try {
            // Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            String saltString = Base64.getEncoder().encodeToString(salt);

            // Hash password with salt
            String hash = hashWithSalt(password, saltString);

            // Return salt:hash format
            return saltString + ":" + hash;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            // Fallback to simple hash
            return "SIMPLE:" + Integer.toHexString(password.hashCode());
        }
    }

    /**
     * Verify a password against a stored hash
     */
    public boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || password == null) {
            return false;
        }

        // Handle old simple hash format for backward compatibility
        if (storedHash.startsWith("SIMPLE:")) {
            String oldHash = storedHash.substring(7);
            return oldHash.equals(Integer.toHexString(password.hashCode()));
        }

        // Handle legacy hash (no salt)
        if (!storedHash.contains(":")) {
            return storedHash.equals(Integer.toHexString(password.hashCode()));
        }

        try {
            // Split salt and hash
            String[] parts = storedHash.split(":", 2);
            if (parts.length != 2) {
                return false;
            }

            String salt = parts[0];
            String expectedHash = parts[1];

            // Hash the provided password with the stored salt
            String actualHash = hashWithSalt(password, salt);

            return expectedHash.equals(actualHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hash a password with a given salt
     */
    private String hashWithSalt(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashedBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    /**
     * Validate email format
     */
    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate password strength
     * Requirements: At least 6 characters, contains letters and numbers
     */
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Get password validation requirements as a string
     */
    public String getPasswordRequirements() {
        return "Password must be at least 6 characters and contain both letters and numbers";
    }

    /**
     * Sanitize input to prevent injection attacks
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'%;()&+]", "");
    }

    /**
     * Generate a new session token for a user
     */
    public SessionToken generateSessionToken(Long userId) {
        SessionToken token = new SessionToken(userId, SESSION_EXPIRY_MINUTES);
        activeSessions.put(token.getToken(), token);
        return token;
    }

    /**
     * Validate a session token
     */
    public boolean validateToken(String tokenString) {
        SessionToken token = activeSessions.get(tokenString);
        if (token == null) {
            return false;
        }

        if (token.isExpired()) {
            activeSessions.remove(tokenString);
            return false;
        }

        return true;
    }

    /**
     * Get user ID from a valid token
     */
    public Long getUserIdFromToken(String tokenString) {
        SessionToken token = activeSessions.get(tokenString);
        if (token != null && !token.isExpired()) {
            return token.getUserId();
        }
        return null;
    }

    /**
     * Invalidate a session token (logout)
     */
    public void invalidateToken(String tokenString) {
        activeSessions.remove(tokenString);
    }

    /**
     * Clean up expired tokens
     */
    public void cleanupExpiredTokens() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Generate a random verification code (6 digits)
     */
    public String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
