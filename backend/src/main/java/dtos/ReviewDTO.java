package dtos;

public record ReviewDTO(Long reviewerId, Long targetUserId, double rating, String comment, long timestamp) {
}
