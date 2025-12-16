package dtos;

public record ReviewRequest(Long targetId, double rating, String comment) {
}
