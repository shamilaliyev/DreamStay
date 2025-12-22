package dtos;

import models.User;
import models.Property;
import models.Report;
import models.Message;
import models.Review;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Collections;

@Component
public class DtoMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null)
            return null;
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isVerified(),
                user.isEmailVerified(),
                user.isIdVerified(),
                user.isApproved(),
                user.getAverageRating(),
                user.getReviewCount(),
                user.getGovernmentId(),
                user.getIdDocumentPath(),
                toPublicUrl(user.getAvatarPath()), // Use public URL for avatar
                user.getBio(),
                user.getPhoneNumber(),
                user.getCity(),
                user.getOccupation());
    }

    public PublicUserDTO toPublicUserDTO(User user) {
        if (user == null)
            return null;
        return new PublicUserDTO(
                user.getId(),
                user.getName(),
                user.getRole(),
                toPublicUrl(user.getAvatarPath()),
                user.getBio(),
                user.getCity(),
                user.getOccupation(),
                user.getAverageRating(),
                user.getReviewCount(),
                user.isVerified(),
                user.isIdVerified());
    }

    public PropertyDTO toPropertyDTO(Property property) {
        if (property == null)
            return null;
        return new PropertyDTO(
                property.getId(),
                property.getOwnerId(),
                property.getTitle(),
                property.getLocation(),
                property.getPrice(),
                property.getRooms(),
                property.getFloor(),
                property.getDescription(),
                property.getArea() != null ? property.getArea() : 0.0,
                property.getPhotos() != null ? property.getPhotos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getVideos() != null ? property.getVideos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getDistanceToMetro() != null ? property.getDistanceToMetro() : 0.0,
                property.getDistanceToUniversity() != null ? property.getDistanceToUniversity() : 0.0,
                property.isArchived(),
                property.isVerified(),
                property.getRatingAverage(),
                property.getRatingCount());
    }

    public MessageDTO toMessageDTO(Message message) {
        if (message == null)
            return null;
        return new MessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getRecipientId(),
                message.getPropertyId(),
                message.getText(),
                message.getTimestamp(),
                message.isRead());
    }

    public ReportDTO toReportDTO(Report report) {
        if (report == null)
            return null;
        return new ReportDTO(
                report.getId(),
                report.getReporterId(),
                report.getReportedUserId(),
                report.getReportedPropertyId(),
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getAdminNotes(),
                report.getTimestamp());
    }

    public ReviewDTO toReviewDTO(Review review) {
        if (review == null)
            return null;
        return new ReviewDTO(
                review.getReviewerId(),
                review.getTargetUserId(),
                review.getRating(),
                review.getComment(),
                0L // Review model doesn't have timestamp yet, returning 0
        );
    }

    public AdminUnverifiedPropertyDTO toAdminUnverifiedPropertyDTO(Property property, User owner) {
        if (property == null || owner == null)
            return null;

        return new AdminUnverifiedPropertyDTO(
                property.getId(),
                property.getTitle(),
                property.getLocation(),
                property.getPrice(),
                property.getRooms(),
                property.getFloor(),
                property.getDescription(),
                property.getArea() != null ? property.getArea() : 0.0,
                property.getPhotos() != null ? property.getPhotos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getVideos() != null ? property.getVideos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getDistanceToMetro() != null ? property.getDistanceToMetro() : 0.0,
                property.getDistanceToUniversity() != null ? property.getDistanceToUniversity() : 0.0,
                property.isVerified(),
                property.isArchived(),

                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getRole(),
                owner.isVerified(),
                owner.isIdVerified(),
                owner.isApproved(),
                owner.getAverageRating());
    }

    public PropertyWithOwnerDTO toPropertyWithOwnerDTO(Property property, User owner) {
        if (property == null || owner == null)
            return null;

        return new PropertyWithOwnerDTO(
                property.getId(),
                property.getOwnerId(),
                property.getTitle(),
                property.getLocation(),
                property.getPrice(),
                property.getRooms(),
                property.getFloor(),
                property.getDescription(),
                property.getArea() != null ? property.getArea() : 0.0,
                property.getPhotos() != null ? property.getPhotos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getVideos() != null ? property.getVideos().stream().map(this::toPublicUrl).toList()
                        : Collections.emptyList(),
                property.getDistanceToMetro() != null ? property.getDistanceToMetro() : 0.0,
                property.getDistanceToUniversity() != null ? property.getDistanceToUniversity() : 0.0,
                property.isArchived(),
                property.isVerified(),
                property.getRatingAverage(),
                property.getRatingCount(),

                owner.getName(),
                owner.getEmail(),
                owner.getRole(),
                owner.isVerified(),
                owner.getAverageRating(),
                owner.getReviewCount());
    }

    private String toPublicUrl(String path) {
        if (path == null)
            return null;
        if (path.startsWith("http"))
            return path;
        // Ensure path starts with / for URL
        String cleanPath = path.startsWith("/") ? path : "/" + path;
        return "http://localhost:8080" + cleanPath;
    }
}
