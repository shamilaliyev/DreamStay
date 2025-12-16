package services;

import models.User;

/**
 * Service for handling email verification workflow (FR-01, FR-02)
 * Generates verification codes and validates them
 */
public class EmailVerificationService {
    private static final int CODE_EXPIRY_MINUTES = 15;
    private SecurityService securityService;

    public EmailVerificationService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Generate and send a verification code to the user's email
     */
    public void sendVerificationCode(User user) {
        // Generate 6-digit code
        String code = securityService.generateVerificationCode();

        // Set code and expiry on user
        user.setVerificationCode(code);
        long expiryTime = System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60 * 1000);
        user.setVerificationCodeExpiry(expiryTime);

        // Display verification code (in console app, we can't actually send email)
        System.out.println("\n========================================");
        System.out.println("EMAIL VERIFICATION");
        System.out.println("========================================");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Email Verification Code");
        System.out.println("\nYour verification code is: " + code);
        System.out.println("This code will expire in " + CODE_EXPIRY_MINUTES + " minutes.");
        System.out.println("========================================\n");
    }

    /**
     * Verify a code entered by the user
     */
    public boolean verifyCode(User user, String enteredCode) {
        if (user.getVerificationCode() == null) {
            System.out.println("No verification code found for this user.");
            return false;
        }

        // Check if code has expired
        if (System.currentTimeMillis() > user.getVerificationCodeExpiry()) {
            System.out.println("Verification code has expired.");
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(0);
            return false;
        }

        // Check if code matches
        if (user.getVerificationCode().equals(enteredCode)) {
            user.setEmailVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(0);
            System.out.println("Email verified successfully!");
            return true;
        } else {
            System.out.println("Invalid verification code.");
            return false;
        }
    }

    /**
     * Check if a verification code is still valid
     */
    public boolean isCodeValid(User user) {
        if (user.getVerificationCode() == null) {
            return false;
        }
        return System.currentTimeMillis() <= user.getVerificationCodeExpiry();
    }

    /**
     * Resend verification code
     */
    public void resendVerificationCode(User user) {
        sendVerificationCode(user);
    }
}
