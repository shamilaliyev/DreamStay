package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file uploads
 * Saves uploaded images to the file system and returns their paths
 * 
 * This service handles:
 * - Saving uploaded files to disk
 * - Generating unique file names
 * - Validating file types
 * - Creating upload directories
 */
public class FileUploadService {

    // Base directory for all uploads
    private static final String UPLOAD_BASE_DIR = "uploads/properties/";

    // Allowed image extensions
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

    // Allowed video extensions
    private static final String[] ALLOWED_VIDEO_EXTENSIONS = { ".mp4", ".avi", ".mov", ".webm", ".mkv" };

    // Maximum file size for images (5MB)
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    // Maximum file size for videos (50MB)
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    // Secure directory for identity documents (not served publicly by default
    // WebConfig)
    private static final String ID_DOC_DIR = "identity-docs/";

    public FileUploadService() {
        // Create upload directories
        createUploadDirectory();
        createDirectory(ID_DOC_DIR);
    }

    /**
     * Save ID document in a secure location
     */
    public String saveIdDocument(Long userId, byte[] fileData, String originalFileName) throws IOException {
        // ... (existing implementation)
        // Re-declaring entire method for context if needed, but I'll use target
        // correctly
        // Just adding saveAvatar below saveIdDocument
        if (fileData.length > MAX_IMAGE_SIZE) {
            throw new IOException("File too large (max 5MB)");
        }
        if (!isValidImageFile(originalFileName)) {
            throw new IOException("Invalid file type");
        }

        String extension = getFileExtension(originalFileName);
        String uniqueFileName = "id_" + userId + "_" + UUID.randomUUID().toString() + extension;
        String filePath = ID_DOC_DIR + uniqueFileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
        }

        return filePath;
    }

    /**
     * Save Avatar image
     */
    public String saveAvatar(Long userId, byte[] fileData, String originalFileName) throws IOException {
        if (fileData.length > MAX_IMAGE_SIZE) {
            throw new IOException("File too large (max 5MB)");
        }
        if (!isValidImageFile(originalFileName)) {
            throw new IOException("Invalid file type");
        }

        String extension = getFileExtension(originalFileName);
        // Use consistent naming (e.g. avatar_123.jpg) or unique if historical
        // Overwriting is cleaner for avatars to save space, but browser caching might
        // be an issue.
        // Using UUID helps with cache busting.
        String uniqueFileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + extension;
        String uploadDir = "uploads/avatars/";
        createDirectory(uploadDir);
        String filePath = uploadDir + uniqueFileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
        }

        return filePath;
    }

    // ... existing methods ...

    /**
     * Save multiple property photos
     * 
     * @param propertyId - ID of the property
     * @param files      - Array of file data (bytes)
     * @param fileNames  - Original file names
     * @return List of saved file paths
     */
    public List<String> savePropertyPhotos(Long propertyId, byte[][] files, String[] fileNames) {
        List<String> savedPaths = new ArrayList<>();

        if (files.length != fileNames.length) {
            throw new IllegalArgumentException("Files and fileNames arrays must have the same length");
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String savedPath = saveFile(propertyId, files[i], fileNames[i]);
                savedPaths.add(savedPath);
            } catch (IOException e) {
                System.err.println("Failed to save file: " + fileNames[i] + " - " + e.getMessage());
                // Continue with other files
            }
        }

        return savedPaths;
    }

    /**
     * Save a single image file
     * 
     * @param propertyId       - ID of the property
     * @param fileData         - File content as bytes
     * @param originalFileName - Original file name
     * @return Path where the file was saved
     */
    private String saveFile(Long propertyId, byte[] fileData, String originalFileName) throws IOException {
        // Validate file size
        if (fileData.length > MAX_IMAGE_SIZE) {
            throw new IOException("File too large: " + originalFileName + " (max 5MB)");
        }

        // Validate file extension
        if (!isValidImageFile(originalFileName)) {
            throw new IOException("Invalid file type: " + originalFileName);
        }

        // Generate unique file name
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Create property-specific directory
        String propertyDir = UPLOAD_BASE_DIR + propertyId + "/";
        createDirectory(propertyDir);

        // Full file path
        String filePath = propertyDir + uniqueFileName;

        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
        }

        // Return relative path (for storing in database)
        return filePath;
    }

    /**
     * Save multiple property videos
     * 
     * @param propertyId - ID of the property
     * @param files      - Array of file data (bytes)
     * @param fileNames  - Original file names
     * @return List of saved file paths
     */
    public List<String> savePropertyVideos(Long propertyId, byte[][] files, String[] fileNames) {
        List<String> savedPaths = new ArrayList<>();

        if (files.length != fileNames.length) {
            throw new IllegalArgumentException("Files and fileNames arrays must have the same length");
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String savedPath = saveVideoFile(propertyId, files[i], fileNames[i]);
                savedPaths.add(savedPath);
            } catch (IOException e) {
                System.err.println("Failed to save video: " + fileNames[i] + " - " + e.getMessage());
                // Continue with other files
            }
        }

        return savedPaths;
    }

    /**
     * Save a single video file
     * 
     * @param propertyId       - ID of the property
     * @param fileData         - File content as bytes
     * @param originalFileName - Original file name
     * @return Path where the file was saved
     */
    private String saveVideoFile(Long propertyId, byte[] fileData, String originalFileName) throws IOException {
        // Validate file size
        if (fileData.length > MAX_VIDEO_SIZE) {
            throw new IOException("Video file too large: " + originalFileName + " (max 50MB)");
        }

        // Validate file extension
        if (!isValidVideoFile(originalFileName)) {
            throw new IOException("Invalid video file type: " + originalFileName);
        }

        // Generate unique file name
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = "video_" + UUID.randomUUID().toString() + extension;

        // Create property-specific directory
        String propertyDir = UPLOAD_BASE_DIR + propertyId + "/videos/";
        createDirectory(propertyDir);

        // Full file path
        String filePath = propertyDir + uniqueFileName;

        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
        }

        // Return relative path (for storing in database)
        return filePath;
    }

    /**
     * Check if file has a valid image extension
     */
    private boolean isValidImageFile(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if file has a valid video extension
     */
    private boolean isValidVideoFile(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        for (String ext : ALLOWED_VIDEO_EXTENSIONS) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file extension from file name
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return "";
    }

    /**
     * Create upload base directory
     */
    private void createUploadDirectory() {
        createDirectory(UPLOAD_BASE_DIR);
    }

    /**
     * Create a directory if it doesn't exist
     */
    private void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Delete a file
     * Useful for cleanup when removing photos
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the public URL for a file
     * When you deploy with a web server, you'll serve files from /uploads
     * 
     * Example: "uploads/properties/123/abc-def.jpg"
     * -> "http://localhost:8080/uploads/properties/123/abc-def.jpg"
     */
    public String getPublicUrl(String filePath) {
        if (filePath == null)
            return null;
        // Do NOT expose ID docs publicly
        if (filePath.startsWith(ID_DOC_DIR)) {
            return null;
        }

        // In production, replace with your actual domain
        String baseUrl = "http://localhost:8080/";
        return baseUrl + filePath;
    }
}
