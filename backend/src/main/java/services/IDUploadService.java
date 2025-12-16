package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling ID document uploads (FR-01, FR-02)
 * Stores documents in the file system for admin verification
 */
public class IDUploadService {
    private static final String UPLOAD_BASE_DIR = "uploads/id_documents/";
    private static final String[] ALLOWED_EXTENSIONS = { ".pdf", ".jpg", ".jpeg", ".png" };
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public IDUploadService() {
        createUploadDirectory();
    }

    /**
     * Upload an ID document file
     * 
     * @param userId           - ID of the user uploading the document
     * @param fileData         - File content as bytes
     * @param originalFileName - Original file name
     * @return Path where the file was saved
     */
    public String uploadIDDocument(Long userId, byte[] fileData, String originalFileName) throws IOException {
        // Validate file size
        if (fileData.length > MAX_FILE_SIZE) {
            throw new IOException("File too large: " + originalFileName + " (max 10MB)");
        }

        // Validate file extension
        if (!isValidDocumentFile(originalFileName)) {
            throw new IOException("Invalid file type: " + originalFileName + ". Allowed: PDF, JPG, PNG");
        }

        // Generate unique file name
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = "ID_" + userId + "_" + UUID.randomUUID().toString() + extension;

        // Create user-specific directory
        String userDir = UPLOAD_BASE_DIR + userId + "/";
        createDirectory(userDir);

        // Full file path
        String filePath = userDir + uniqueFileName;

        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
        }

        System.out.println("ID document uploaded successfully: " + filePath);
        return filePath;
    }

    /**
     * Upload ID document from file path (for console application)
     */
    public String uploadIDDocumentFromPath(Long userId, String sourceFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);

        if (!sourceFile.exists()) {
            throw new IOException("File not found: " + sourceFilePath);
        }

        byte[] fileData = Files.readAllBytes(sourceFile.toPath());
        return uploadIDDocument(userId, fileData, sourceFile.getName());
    }

    /**
     * Check if file has a valid document extension
     */
    private boolean isValidDocumentFile(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
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
     * Delete an ID document
     */
    public boolean deleteDocument(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete document: " + filePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the document path for a user
     */
    public String getDocumentPath(Long userId, String fileName) {
        return UPLOAD_BASE_DIR + userId + "/" + fileName;
    }
}
