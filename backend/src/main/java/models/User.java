package models;

/**
 * author @shamilaliyev Shami Aliyev
 *
 * The `User` class serves as a base for different user roles (e.g., Admin) and
 * defines common attributes such as ID, name, email, and role.
 *
 * For the Spring Boot REST backend, this class is also a JPA entity stored in
 * the relational database.
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String password;

    // Legacy field - now represents overall verification
    private boolean isVerified;

    // Email verification status
    private boolean emailVerified;

    // ID document verification status
    private boolean idVerified;

    // Admin approval status
    private boolean isApproved;

    private String governmentId;

    // Path to uploaded ID document
    private String idDocumentPath;

    // Renamed from rating for clarity
    private double averageRating;

    private int reviewCount;

    // Temporary email verification code
    private String verificationCode;

    // Expiry timestamp for verification code
    private long verificationCodeExpiry;

    // Profile Fields
    private String avatarPath;
    private String bio;
    private String phoneNumber;
    private String city;
    private String occupation;

    // Service fields removed - Entity should not hold Service references

    // JPA requires a no-arg constructor
    protected User() {
    }

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

    // Setters for services removed

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
