package controllers;

import dtos.DtoMapper;
import dtos.ReviewDTO;
import dtos.ReviewRequest;
import models.Review;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;
    private final DtoMapper dtoMapper;

    public ReviewController(ReviewService reviewService, AuthService authService, DtoMapper dtoMapper) {
        this.reviewService = reviewService;
        this.authService = authService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/contacted-sellers")
    public ResponseEntity<List<dtos.UserDTO>> getContactedSellers(@RequestHeader("X-User-Id") Long userId) {
        List<User> sellers = reviewService.getContactedSellers(userId);
        return ResponseEntity.ok(sellers.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    @GetMapping("/eligible")
    public ResponseEntity<List<dtos.UserDTO>> getEligibleToReview(@RequestHeader("X-User-Id") Long userId) {
        return getContactedSellers(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews.stream()
                .map(dtoMapper::toReviewDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestHeader("X-User-Id") Long reviewerId,
            @RequestBody ReviewRequest request) {

        User reviewer = authService.getUserById(reviewerId);
        if (reviewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Ideally, ReviewService should return a result or throw exception.
        // For now, we call the void method which prints to console, assuming success if
        // no exception.
        // In a real app, we'd refactor ReviewService to return validation errors.

        // Simple pre-validation here to match Service logic roughly
        if (reviewer.getId().equals(request.targetId())) {
            return ResponseEntity.badRequest().body("Cannot review yourself");
        }

        try {
            reviewService.addReview(reviewer, request.targetId(), (int) request.rating(), request.comment());
            return ResponseEntity.ok("Review submitted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
