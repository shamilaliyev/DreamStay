package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find reviews for a target user (e.g. seller)
    List<Review> findByTargetUserId(Long targetUserId);

    // Find reviews written by a specific reviewer
    List<Review> findByReviewerId(Long reviewerId);

    // Check if a review already exists
    boolean existsByReviewerIdAndTargetUserId(Long reviewerId, Long targetUserId);
}
