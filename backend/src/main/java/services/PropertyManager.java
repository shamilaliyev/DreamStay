package services;

import models.Property;
import models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PropertyManager {
    private static final String FILE_PATH = "properties.json";
    private List<Property> properties;

    public PropertyManager() {
        properties = new ArrayList<>();
        loadProperties();
    }

    private void loadProperties() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            parseJson(json.toString());
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
    }

    private void saveProperties() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(toJson());
        } catch (IOException e) {
            System.err.println("Error saving properties: " + e.getMessage());
        }
    }

    private void parseJson(String json) {
        // Simple Parser Logic similar to AuthService
        if (json == null || json.trim().isEmpty() || json.equals("[]"))
            return;

        String inner = json.trim();
        if (inner.startsWith("["))
            inner = inner.substring(1);
        if (inner.endsWith("]"))
            inner = inner.substring(0, inner.length() - 1);

        List<String> propStrings = splitJsonObjects(inner);
        for (String s : propStrings) {
            Property p = parseProperty(s);
            if (p != null)
                properties.add(p);
        }
    }

    private List<String> splitJsonObjects(String inner) {
        List<String> list = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        for (char c : inner.toCharArray()) {
            if (c == '{')
                braceCount++;
            if (c == '}')
                braceCount--;
            current.append(c);
            if (braceCount == 0 && c == '}' && current.length() > 0) {
                list.add(current.toString());
                current = new StringBuilder();
            } else if (braceCount == 0 && c == ',') {
                current = new StringBuilder();
            }
        }
        return list;
    }

    private Property parseProperty(String json) {
        // Parse fields manually including photos array
        try {
            // Extract photos array first if it exists
            List<String> photosList = new ArrayList<>();
            String photosPattern = "\"photos\":\\[([^\\]]*)\\]";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(photosPattern);
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String photosContent = matcher.group(1);
                if (!photosContent.isEmpty()) {
                    String[] photoArray = photosContent.split(",");
                    for (String photo : photoArray) {
                        String cleanPhoto = photo.trim().replace("\"", "");
                        if (!cleanPhoto.isEmpty()) {
                            photosList.add(cleanPhoto);
                        }
                    }
                }
                // Remove photos array from json for simpler parsing
                json = json.replaceAll(",?\"photos\":\\[[^\\]]*\\]", "");
            }

            // Extract videos array
            List<String> videosList = new ArrayList<>();
            String videosPattern = "\"videos\":\\[([^\\]]*)\\]";
            java.util.regex.Pattern vPattern = java.util.regex.Pattern.compile(videosPattern);
            java.util.regex.Matcher vMatcher = vPattern.matcher(json);
            if (vMatcher.find()) {
                String videosContent = vMatcher.group(1);
                if (!videosContent.isEmpty()) {
                    String[] videoArray = videosContent.split(",");
                    for (String video : videoArray) {
                        String cleanVideo = video.trim().replace("\"", "");
                        if (!cleanVideo.isEmpty()) {
                            videosList.add(cleanVideo);
                        }
                    }
                }
                // Remove videos array from json
                json = json.replaceAll(",?\"videos\":\\[[^\\]]*\\]", "");
            }

            json = json.replace("{", "").replace("}", "");
            String[] parts = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            Long id = 0L;
            Long ownerId = 0L;
            String title = "";
            String location = "";
            double price = 0.0;
            int rooms = 0;
            int floor = 0;
            String description = "";
            double distanceToMetro = 0.0;
            double distanceToUniversity = 0.0;
            boolean isVerified = false;
            boolean isArchived = false;

            for (String part : parts) {
                if (part.trim().isEmpty())
                    continue;
                String[] kv = part.split(":", 2);
                if (kv.length < 2)
                    continue;
                String k = kv[0].trim().replace("\"", "");
                String v = kv[1].trim().replace("\"", "");

                switch (k) {
                    case "id" -> id = Long.parseLong(v);
                    case "ownerId" -> ownerId = Long.parseLong(v);
                    case "title" -> title = v;
                    case "location" -> location = v;
                    case "price" -> price = Double.parseDouble(v);
                    case "rooms" -> rooms = Integer.parseInt(v);
                    case "floor" -> floor = Integer.parseInt(v);
                    case "description" -> description = v;
                    case "isVerified" -> isVerified = Boolean.parseBoolean(v);
                    case "isArchived" -> isArchived = Boolean.parseBoolean(v);
                    case "distanceToMetro" -> distanceToMetro = Double.parseDouble(v);
                    case "distanceToUniversity" -> distanceToUniversity = Double.parseDouble(v);
                }
            }
            Property property = new Property(id, ownerId, title, location, price, rooms, floor, description, isArchived,
                    isVerified);
            property.setDistanceToMetro(distanceToMetro);
            property.setDistanceToUniversity(distanceToUniversity);
            property.setPhotos(photosList);
            property.setVideos(videosList);
            return property;
        } catch (Exception e) {
            System.out.println("Error parsing property: " + e.getMessage());
        }
        return null;
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);

            // Build photos array
            StringBuilder photosJson = new StringBuilder("[");
            if (p.getPhotos() != null && !p.getPhotos().isEmpty()) {
                for (int j = 0; j < p.getPhotos().size(); j++) {
                    photosJson.append("\"").append(p.getPhotos().get(j)).append("\"");
                    if (j < p.getPhotos().size() - 1) {
                        photosJson.append(",");
                    }
                }
            }
            photosJson.append("]");

            // Build videos array
            StringBuilder videosJson = new StringBuilder("[");
            if (p.getVideos() != null && !p.getVideos().isEmpty()) {
                for (int j = 0; j < p.getVideos().size(); j++) {
                    videosJson.append("\"").append(p.getVideos().get(j)).append("\"");
                    if (j < p.getVideos().size() - 1) {
                        videosJson.append(",");
                    }
                }
            }
            videosJson.append("]");

            sb.append(String.format(
                    "{\"id\":%d,\"ownerId\":%d,\"title\":\"%s\",\"location\":\"%s\",\"price\":%.2f,\"rooms\":%d,\"floor\":%d,\"description\":\"%s\",\"distanceToMetro\":%.2f,\"distanceToUniversity\":%.2f,\"photos\":%s,\"videos\":%s,\"isVerified\":%b,\"isArchived\":%b}",
                    p.getId(), p.getOwnerId() == null ? 0 : p.getOwnerId(), p.getTitle(), p.getLocation(), p.getPrice(),
                    p.getRooms(), p.getFloor(), p.getDescription() == null ? "" : p.getDescription(),
                    p.getDistanceToMetro(), p.getDistanceToUniversity(), photosJson.toString(), videosJson.toString(),
                    p.isVerified(), p.isArchived()));
            if (i < properties.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public void addProperty(Property property) {
        properties.add(property);
        saveProperties();
    }

    public void editProperty(Long id, String title, String location, double price) {
        getPropertyById(id).ifPresent(p -> {
            p.setTitle(title);
            p.setLocation(location);
            p.setPrice(price);
            saveProperties();
        });
    }

    public void updateProperty(Property property) {
        // Find existing and replace or just save since it's reference based
        // Since we are likely modifying the object from getPropertyById, we just need
        // to save.
        saveProperties();
    }

    public void archiveProperty(Long id) {
        getPropertyById(id).ifPresent(p -> {
            p.setArchived(true);
            saveProperties();
        });
    }

    public List<Property> getProperties() {
        return new ArrayList<>(properties);
    }

    public Optional<Property> getPropertyById(Long id) {
        return properties.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // New Features
    public void deleteProperty(Long id) {
        properties.removeIf(p -> p.getId().equals(id));
        saveProperties();
    }

    public void verifyProperty(Long id) {
        getPropertyById(id).ifPresent(p -> {
            p.setVerified(true);
            saveProperties();
        });
    }

    public List<Property> search(String keyword, Double minPrice, Double maxPrice, Integer rooms) {
        return properties.stream()
                .filter(p -> !p.isArchived()) // Don't show archived
                .filter(p -> p.isVerified()) // Only show verified
                .filter(p -> keyword == null || p.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || p.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .filter(p -> rooms == null || p.getRooms() == rooms)
                .collect(Collectors.toList());
    }

    public List<Property> getMyProperties(Long ownerId) {
        return properties.stream()
                .filter(p -> p.getOwnerId() != null && p.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Property> getUnverifiedProperties() {
        return properties.stream().filter(p -> !p.isVerified()).collect(Collectors.toList());
    }

    // Photo management methods
    public void addPhotoToProperty(Long propertyId, String photoPath) {
        getPropertyById(propertyId).ifPresent(p -> {
            p.addPhoto(photoPath);
            saveProperties();
        });
    }

    public void removePhotoFromProperty(Long propertyId, int photoIndex) {
        getPropertyById(propertyId).ifPresent(p -> {
            if (p.removePhoto(photoIndex)) {
                saveProperties();
            }
        });
    }

    // Video management methods
    public void addVideoToProperty(Long propertyId, String videoPath) {
        getPropertyById(propertyId).ifPresent(p -> {
            p.addVideo(videoPath);
            saveProperties();
        });
    }

    public void removeVideoFromProperty(Long propertyId, int videoIndex) {
        getPropertyById(propertyId).ifPresent(p -> {
            if (p.removeVideo(videoIndex)) {
                saveProperties();
            }
        });
    }
}
