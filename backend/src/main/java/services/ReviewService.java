package services;

import java.util.List;
import java.util.stream.Collectors;

import models.Review;
import models.User;
import repositories.ReviewRepository;

public class ReviewService {

    private final AuthService authService;
    private final MessageRepository messageManager; // Renamed for clarity in my mind, but class is still
                                                    // MessageRepository
    private final ReviewRepository reviewRepository;

    public ReviewService(AuthService authService, MessageRepository messageManager, ReviewRepository reviewRepository) {
        this.authService = authService;
        this.messageManager = messageManager;
        this.reviewRepository = reviewRepository;
    }

    public void addReview(User reviewer, Long targetUserId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        // Prevent self-review
        if (reviewer.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot review yourself.");
        }

        // Ensure buyer has contacted seller (interaction validation)
        if (!messageManager.hasUserContactedSeller(reviewer.getId(), targetUserId)) {
            throw new IllegalArgumentException("You can only review users you have interacted with.");
        }

        // Check if user has already reviewed this target
        if (reviewRepository.existsByReviewerIdAndTargetUserId(reviewer.getId(), targetUserId)) {
            // For simplicity, let's say we update it if it exists, or just block.
            // The old code updated it.
            Review existing = reviewRepository.findByReviewerId(reviewer.getId()).stream()
                    .filter(r -> r.getTargetUserId().equals(targetUserId))
                    .findFirst().orElse(null);

            if (existing != null) {
                existing.setRating(rating);
                existing.setComment(comment);
                reviewRepository.save(existing);
                updateUserRating(targetUserId);
                return;
            }
        }

        Review review = new Review(reviewer.getId(), targetUserId, rating, comment);
        reviewRepository.save(review);

        // Update user's average rating
        updateUserRating(targetUserId);
    }

    /**
     * Compute and update average rating for a user
     */
    private void updateUserRating(Long userId) {
        List<Review> userReviews = getReviewsForUser(userId);

        if (userReviews.isEmpty()) {
            return; // Or set to 0? Keeping old logic.
        }

        double totalRating = userReviews.stream()
                .mapToInt(Review::getRating)
                .sum();

        double averageRating = totalRating / userReviews.size();

        User target = authService.getUserById(userId);

        if (target != null) {
            target.setAverageRating(averageRating);
            target.setReviewCount(userReviews.size());
            // Persist rating changes via AuthService
            authService.updateUser(target);
        }
    }

    /**
     * Get average rating for a user
     */
    public double computeAverageRatingForUser(Long userId) {
        List<Review> userReviews = getReviewsForUser(userId);

        if (userReviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = userReviews.stream()
                .mapToInt(Review::getRating)
                .sum();

        return totalRating / userReviews.size();
    }

    /**
     * Get all reviews for a user
     */
    public List<Review> getUserReviews(Long userId) {
        return getReviewsForUser(userId);
    }

    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepository.findByTargetUserId(userId);
    }

    /**
     * Get list of users (sellers/agents) that the buyer has contacted
     */
    public List<User> getContactedSellers(Long buyerId) {
        return authService.getAllUsers().stream()
                .filter(u -> !u.getId().equals(buyerId)) // Exclude self
                .filter(u -> messageManager.hasUserContactedSeller(buyerId, u.getId()))
                .collect(Collectors.toList());
    }
}
