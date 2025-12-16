package dtos;

import java.time.LocalDateTime;

public record ReportDTO(
        Long id,
        Long reporterId,
        Long reportedUserId,
        Long reportedPropertyId,
        String reason,
        String description,
        String status,
        String adminNotes,
        LocalDateTime timestamp) {
}
