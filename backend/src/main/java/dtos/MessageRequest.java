package dtos;

public record MessageRequest(String recipientEmail, Long recipientId, String content) {
}
