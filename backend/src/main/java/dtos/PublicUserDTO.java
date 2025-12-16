package dtos;

public record PublicUserDTO(
        Long id,
        String name,
        String role,
        String avatarUrl,
        String bio,
        String city,
        String occupation,
        double averageRating,
        int reviewCount,
        boolean isVerified,
        boolean identityVerified) {
}
