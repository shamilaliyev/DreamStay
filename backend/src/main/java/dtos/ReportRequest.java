package dtos;

public record ReportRequest(Long reportedUserId, Long reportedPropertyId, String reason, String description) {
}
