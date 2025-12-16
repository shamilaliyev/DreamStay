package models;

public class Review {
    private Long reviewerId;
    private Long targetUserId;
    private int rating; // 1-5
    private String comment;

    public Review(Long reviewerId, Long targetUserId, int rating, String comment) {
        this.reviewerId = reviewerId;
        this.targetUserId = targetUserId;
        this.rating = rating;
        this.comment = comment;
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
