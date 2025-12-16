package models;

/**
 * Data class encapsulating search criteria for advanced property search
 * Implements Builder pattern for flexible filter construction (FR-05, FR-06)
 */
public class SearchFilter {
    private String keyword;
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private Integer rooms;
    private Integer floor;
    private Double maxDistanceToMetro; // in km
    private Double maxDistanceToUniversity; // in km

    // Private constructor for builder pattern
    private SearchFilter(Builder builder) {
        this.keyword = builder.keyword;
        this.location = builder.location;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.rooms = builder.rooms;
        this.floor = builder.floor;
        this.maxDistanceToMetro = builder.maxDistanceToMetro;
        this.maxDistanceToUniversity = builder.maxDistanceToUniversity;
    }

    // Getters
    public String getKeyword() {
        return keyword;
    }

    public String getLocation() {
        return location;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public Integer getRooms() {
        return rooms;
    }

    public Integer getFloor() {
        return floor;
    }

    public Double getMaxDistanceToMetro() {
        return maxDistanceToMetro;
    }

    public Double getMaxDistanceToUniversity() {
        return maxDistanceToUniversity;
    }

    // Builder class
    public static class Builder {
        private String keyword;
        private String location;
        private Double minPrice;
        private Double maxPrice;
        private Integer rooms;
        private Integer floor;
        private Double maxDistanceToMetro;
        private Double maxDistanceToUniversity;

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder minPrice(Double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder rooms(Integer rooms) {
            this.rooms = rooms;
            return this;
        }

        public Builder floor(Integer floor) {
            this.floor = floor;
            return this;
        }

        public Builder maxDistanceToMetro(Double maxDistanceToMetro) {
            this.maxDistanceToMetro = maxDistanceToMetro;
            return this;
        }

        public Builder maxDistanceToUniversity(Double maxDistanceToUniversity) {
            this.maxDistanceToUniversity = maxDistanceToUniversity;
            return this;
        }

        public SearchFilter build() {
            return new SearchFilter(this);
        }
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
                "keyword='" + keyword + '\'' +
                ", location='" + location + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", rooms=" + rooms +
                ", floor=" + floor +
                ", maxDistanceToMetro=" + maxDistanceToMetro +
                ", maxDistanceToUniversity=" + maxDistanceToUniversity +
                '}';
    }
}
