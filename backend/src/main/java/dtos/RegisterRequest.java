package dtos;

public record RegisterRequest(String name, String email, String password, String role, String governmentId) {
}
