package dtos;

import java.util.List;

public record AdminUnverifiedPropertyDTO(
        // Property Details
        Long id,
        String title,
        String location,
        double price,
        int rooms,
        int floor,
        String description,
        List<String> photos,
        List<String> videos,
        double distanceToMetro,
        double distanceToUniversity,
        boolean isVerified,
        boolean isArchived,

        // Owner Details
        Long ownerId,
        String ownerName,
        String ownerEmail,
        String ownerRole,
        boolean isOwnerVerified,
        boolean isOwnerIdVerified,
        boolean isOwnerApproved,
        double ownerRating) {
}
