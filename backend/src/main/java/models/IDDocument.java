package models;

import java.time.LocalDateTime;

/**
 * Represents an identity document uploaded by a user for verification
 * Used in the admin approval workflow (FR-01, FR-02)
 */
public class IDDocument {
    private Long id;
    private Long userId;
    private String documentType; // e.g., "Passport", "National ID", "Driver's License"
    private String documentPath; // File path to the uploaded document
    private LocalDateTime uploadDate;
    private String verificationStatus; // "PENDING", "APPROVED", "REJECTED"
    private String rejectionReason; // Optional reason if rejected

    public IDDocument(Long id, Long userId, String documentType, String documentPath) {
        this.id = id;
        this.userId = userId;
        this.documentType = documentType;
        this.documentPath = documentPath;
        this.uploadDate = LocalDateTime.now();
        this.verificationStatus = "PENDING";
        this.rejectionReason = null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "IDDocument{" +
                "id=" + id +
                ", userId=" + userId +
                ", documentType='" + documentType + '\'' +
                ", documentPath='" + documentPath + '\'' +
                ", uploadDate=" + uploadDate +
                ", verificationStatus='" + verificationStatus + '\'' +
                '}';
    }
}
