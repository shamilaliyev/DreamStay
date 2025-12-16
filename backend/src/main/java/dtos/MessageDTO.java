package dtos;

import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        Long senderId,
        Long recipientId,
        Long propertyId,
        String text,
        LocalDateTime timestamp,
        boolean isRead) {
}
