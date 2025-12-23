package models;

/**
 * author @frhetto Nijat Alisoy
 * The Property class represents a real estate property with attributes such as id,
 * name, location, price, and an isArchived status. It provides:
 *  Constructors: To initialize property details.
 * Getters and Setters: To access and modify property attributes.
 * Archival Management: Supports marking a property as archived.
 * toString Method: For easy display of property details.
 * This class encapsulates property-related data
 * and serves as a foundational entity for real estate operations.
 */

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the property

    private Long ownerId; // ID of the Seller or Agent who owns this listing

    private String title; // Title of the property

    private String location; // Location of the property

    private double price; // Price of the property

    private int rooms;

    private int floor;

    @Column(length = 2000) // Allow longer descriptions
    private String description;

    @Column(nullable = true)
    private Double area; // Area in square meters

    @ElementCollection
    private List<String> photos = new ArrayList<>();

    @ElementCollection
    private List<String> videos = new ArrayList<>(); // Video file paths

    @Column(nullable = true)
    private Double distanceToMetro; // Distance to nearest metro station in km

    @Column(nullable = true)
    private Double distanceToUniversity; // Distance to nearest university in km

    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    private boolean isArchived; // Flag to indicate if the property is archived

    private boolean isVerified;

    private double ratingAverage;

    private int ratingCount;

    // JPA requires a no-arg constructor
    protected Property() {
    }

    // Constructor
    public Property(Long id, Long ownerId, String title, String location, double price, int rooms, int floor,
            String description, Double area, boolean isArchived, boolean isVerified) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.location = location;
        this.price = price;
        this.rooms = rooms;
        this.floor = floor;
        this.description = description;
        this.area = area;
        this.photos = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.distanceToMetro = 0.0;
        this.distanceToUniversity = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.isArchived = isArchived;
        this.isVerified = isVerified;
        this.ratingAverage = 0.0;
        this.ratingCount = 0;
    }

    // Constructor for Property Creation (PropertyController)
    public Property(Long ownerId, String title, String location, Double price, Integer rooms, Integer floor,
            String description, Double area, List<String> photos, List<String> videos, Double distanceToMetro,
            Double distanceToUniversity, Double latitude, Double longitude) {
        this.ownerId = ownerId;
        this.title = title;
        this.location = location;
        this.price = price;
        this.rooms = rooms;
        this.floor = floor;
        this.description = description;
        this.area = area;
        this.photos = photos != null ? photos : new ArrayList<>();
        this.videos = videos != null ? videos : new ArrayList<>();
        this.distanceToMetro = distanceToMetro;
        this.distanceToUniversity = distanceToUniversity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isArchived = false;
        this.isVerified = false;
        this.ratingAverage = 0.0;
        this.ratingCount = 0;
    }

    public Property(long timeMillis, String name2, String location2, double price2) {
        // Keeping this for backward compatibility (or temporary fix until refactor)
        // Defaulting missing values
        this(timeMillis, null, name2, location2, price2, 0, 0, "", 0.0, false, false);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @deprecated Use getTitle() instead
     */
    @Deprecated
    public String getName() {
        return title;
    }

    /**
     * @deprecated Use setTitle() instead
     */
    @Deprecated
    public void setName(String name) {
        this.title = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public Double getDistanceToMetro() {
        return distanceToMetro;
    }

    public void setDistanceToMetro(Double distanceToMetro) {
        this.distanceToMetro = distanceToMetro;
    }

    public Double getDistanceToUniversity() {
        return distanceToUniversity;
    }

    public void setDistanceToUniversity(Double distanceToUniversity) {
        this.distanceToUniversity = distanceToUniversity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    // toString method for displaying property details
    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", price=" + price +
                ", rooms=" + rooms +
                ", floor=" + floor +
                ", description='" + description + '\'' +
                ", isVerified=" + isVerified +
                ", isArchived=" + isArchived +
                ", rating=" + ratingAverage +
                '}';
    }

    // Photo management helper methods
    /**
     * Add a photo to the property
     */
    public void addPhoto(String photoPath) {
        if (photoPath != null && !photoPath.trim().isEmpty()) {
            this.photos.add(photoPath.trim());
        }
    }

    /**
     * Remove a photo by index
     */
    public boolean removePhoto(int index) {
        if (index >= 0 && index < photos.size()) {
            photos.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Check if property has photos
     */
    public boolean hasPhotos() {
        return photos != null && !photos.isEmpty();
    }

    // Video management helper methods
    /**
     * Add a video to the property
     */
    public void addVideo(String videoPath) {
        if (videoPath != null && !videoPath.trim().isEmpty()) {
            this.videos.add(videoPath.trim());
        }
    }

    /**
     * Remove a video by index
     */
    public boolean removeVideo(int index) {
        if (index >= 0 && index < videos.size()) {
            videos.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Check if property has videos
     */
    public boolean hasVideos() {
        return videos != null && !videos.isEmpty();
    }
}
