package models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reviewerId;
    private Long targetUserId;
    private int rating; // 1-5
    private String comment;

    protected Review() {
    }

    public Review(Long reviewerId, Long targetUserId, int rating, String comment) {
        this.reviewerId = reviewerId;
        this.targetUserId = targetUserId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewer=" + reviewerId +
                ", target=" + targetUserId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
