package dtos;

public record UserDTO(
                Long id,
                String name,
                String email,
                String role,
                boolean isVerified,
                boolean emailVerified,
                boolean idVerified,
                boolean isApproved,
                double averageRating,
                int reviewCount,
                String governmentId,
                String idDocumentPath,
                // Profile Fields
                String avatarUrl,
                String bio,
                String phoneNumber,
                String city,
                String occupation) {
}
