package dtos;

import java.util.List;

public record PropertyWithOwnerDTO(
                Long id,
                Long ownerId,
                String title,
                String location,
                double price,
                int rooms,
                int floor,
                String description,
                double area,
                List<String> photos,
                List<String> videos,
                double distanceToMetro,
                double distanceToUniversity,
                boolean isArchived,
                boolean isVerified,
                double ratingAverage,
                int ratingCount,

                // Owner Details
                String ownerName,
                String ownerEmail,
                String ownerRole,
                boolean isOwnerVerified,
                double ownerRating,
                int ownerReviewCount) {
}
