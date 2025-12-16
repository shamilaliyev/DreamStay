package services;

import models.User;
import models.Property;
import java.util.List;

public class VerificationService {
    private AuthService authService;
    private PropertyManager propertyManager;

    public VerificationService(AuthService authService, PropertyManager propertyManager) {
        this.authService = authService;
        this.propertyManager = propertyManager;
    }

    // User Verification
    public List<User> getAllUsers() {
        return authService.getAllUsers();
    }

    public List<User> getUnverifiedUsers() {
        return authService.getUnverifiedUsers();
    }

    public void verifyUser(Long userId) {
        authService.verifyUser(userId);
    }

    public void rejectUser(Long userId) {
        // Implement rejection (maybe delete or mark rejected)
        System.out.println("User " + userId + " rejected.");
        // authService.deleteUser(userId); // If implemented
    }

    // Property Verification
    public List<Property> getUnverifiedProperties() {
        return propertyManager.getUnverifiedProperties();
    }

    public void verifyProperty(Long propertyId) {
        propertyManager.verifyProperty(propertyId);
    }

    public void rejectProperty(Long propertyId) {
        System.out.println("Property " + propertyId + " rejected.");
        propertyManager.deleteProperty(propertyId);
    }

    public List<User> getUnverifiedAdmins() {
        return authService.getUnverifiedAdmins();
    }

    public void verifyAdmin(Long userId) {
        authService.approveUser(userId); // Admin verification is essentially approval
    }

    /**
     * Get the AuthService instance (for admin checks)
     */
    public AuthService getAuthService() {
        return authService;
    }
}
