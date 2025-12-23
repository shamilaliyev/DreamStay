package dtos;

import java.util.List;

public record PropertyDTO(
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
        Double latitude,
        Double longitude,
        boolean isArchived,
        boolean isVerified,
        double ratingAverage,
        int ratingCount) {
}
