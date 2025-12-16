package models;

import java.time.LocalDateTime;

/**
 * Represents a user report for inappropriate content or behavior
 * Used in admin moderation panel (FR-09, FR-10, FR-11)
 */
public class Report {
    private Long id;
    private Long reporterId; // User who created the report
    private Long reportedUserId; // User being reported (can be null if reporting property)
    private Long reportedPropertyId; // Property being reported (can be null if reporting user)
    private String reason; // "SPAM", "INAPPROPRIATE", "FRAUD", "OTHER"
    private String description; // Detailed description of the issue
    private LocalDateTime timestamp;
    private String status; // "PENDING", "REVIEWED", "RESOLVED", "DISMISSED"
    private String adminNotes; // Notes added by admin during review

    public Report(Long id, Long reporterId, Long reportedUserId, Long reportedPropertyId,
            String reason, String description) {
        this.id = id;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reportedPropertyId = reportedPropertyId;
        this.reason = reason;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.adminNotes = null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public Long getReportedPropertyId() {
        return reportedPropertyId;
    }

    public void setReportedPropertyId(Long reportedPropertyId) {
        this.reportedPropertyId = reportedPropertyId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", reporterId=" + reporterId +
                ", reportedUserId=" + reportedUserId +
                ", reportedPropertyId=" + reportedPropertyId +
                ", reason='" + reason + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
