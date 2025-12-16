package models;

/** 
* author @shamilaliyev Shami Aliyev
*
 * The `User` class serves as a base for different user roles (e.g., Admin) and defines common attributes
 * such as ID, name, email, and role. It also integrates messaging functionality via the `MailService`.
 * 
 * Features:
 * - Common attributes: ID, name, email, and role with getter/setter methods.
 * - Shared `MailService` for messaging between users.
 * - Abstract `showMenu` method to enforce implementation in subclasses.
 * - Provides a string representation of the user for easy display.
 */

import services.MailService;
import services.PropertyManager;
import services.ReviewService;
import services.VerificationService;
import java.util.List;
import models.Message;

public abstract class User {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String password;
    private boolean isVerified; // Legacy field - now represents overall verification
    private boolean emailVerified; // Email verification status
    private boolean idVerified; // ID document verification status
    private boolean isApproved; // Admin approval status
    private String governmentId;
    private String idDocumentPath; // Path to uploaded ID document
    private double averageRating; // Renamed from rating for clarity
    private int reviewCount;
    private String verificationCode; // Temporary email verification code
    private long verificationCodeExpiry; // Expiry timestamp for verification code

    // Profile Fields
    private String avatarPath;
    private String bio;
    private String phoneNumber;
    private String city;
    private String occupation;

    protected MailService mailService;
    protected PropertyManager propertyManager;
    protected ReviewService reviewService;
    protected VerificationService verificationService;

    public User(Long id, String name, String email, String role, String password, boolean isVerified,
            String governmentId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.isVerified = isVerified;
        this.emailVerified = false;
        this.idVerified = false;
        this.isApproved = false;
        this.governmentId = governmentId;
        this.idDocumentPath = null;
        this.averageRating = 0.0;
        this.reviewCount = 0;
        this.verificationCode = null;
        this.verificationCodeExpiry = 0;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    // Getters and Setters for fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isIdVerified() {
        return idVerified;
    }

    public void setIdVerified(boolean idVerified) {
        this.idVerified = idVerified;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getIdDocumentPath() {
        return idDocumentPath;
    }

    public void setIdDocumentPath(String idDocumentPath) {
        this.idDocumentPath = idDocumentPath;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // Legacy getter for compatibility
    public double getRating() {
        return averageRating;
    }

    // Legacy setter for compatibility
    public void setRating(double rating) {
        this.averageRating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public long getVerificationCodeExpiry() {
        return verificationCodeExpiry;
    }

    public void setVerificationCodeExpiry(long verificationCodeExpiry) {
        this.verificationCodeExpiry = verificationCodeExpiry;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    // Chat UI removed for Spring Boot migration

    // Abstract method to display the menu for the user

    // toString method to provide a string representation of the user
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isVerified=" + isVerified +
                ", emailVerified=" + emailVerified +
                ", idVerified=" + idVerified +
                ", isApproved=" + isApproved +
                ", rating=" + averageRating +
                '}';
    }
}
