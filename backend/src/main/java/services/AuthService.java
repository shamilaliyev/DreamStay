package services;

/**
 * author @frhetto Nijat Alisoy
 * This class authenticates the users that are in the users.json file (database)
 * User Management: Stores and handles User objects.
 * Data Persistence: Reads and writes user data to a file.
 * Login: Authenticates users by email.
 * Default Users: Initializes with default users if no data exists.
 * JSON Handling: Serializes and parses user data in JSON format.
 * Error Handling: Handles file and data-related issues gracefully.
 * 
 */

import models.*;
import exceptions.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class AuthService {
    private static final String FILE_PATH = "users.json";
    private List<User> users;
    private SecurityService securityService;

    public AuthService(SecurityService securityService) {
        this.securityService = securityService;
        users = new ArrayList<>();
        loadUsers();
        // Ensure default admin exists and is verified
        if (users.stream().noneMatch(u -> u.getRole().equalsIgnoreCase("admin"))) {
            initializeDefaultUsers();
            saveUsers();
        }
    }

    // Constructor for backward compatibility (creates its own SecurityService)
    public AuthService() {
        this(new SecurityService());
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        System.out.println("DEBUG: Loading users from: " + file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println("DEBUG: File not found. creating new defaults.");
            initializeDefaultUsers();
            saveUsers();
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);

            users.clear();
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    try {
                        // Use manual parsing helper to avoid need for default constructors in models
                        User user = parseUserFromNode(node);

                        if (user != null) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        System.err.println("Skipping invalid user node: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
            mapper.writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // Helper to keep using existing constructors without modifying Model classes
    // substantially
    private User parseUserFromNode(JsonNode node) {
        Long id = node.has("id") ? node.get("id").asLong() : 0L;
        String name = node.has("name") ? node.get("name").asText() : "";
        String email = node.has("email") ? node.get("email").asText() : "";
        String role = node.has("role") ? node.get("role").asText() : "";
        String password = node.has("password") ? node.get("password").asText() : "";
        boolean isVerified = node.has("verified") ? node.get("verified").asBoolean()
                : (node.has("isVerified") ? node.get("isVerified").asBoolean() : false);
        String govId = node.has("governmentId") ? node.get("governmentId").asText() : null;
        if ("null".equals(govId))
            govId = null;

        User u = switch (role.toLowerCase()) {
            case "admin" -> new Admin(id, name, email, password, isVerified, govId);
            case "buyer" -> new Buyer(id, name, email, password, isVerified, govId);
            case "seller" -> new Seller(id, name, email, password, isVerified, govId);
            case "agent" -> new Agent(id, name, email, password, isVerified, govId);
            default -> null;
        };

        if (u != null) {
            if (node.has("averageRating"))
                u.setAverageRating(node.get("averageRating").asDouble());
            if (node.has("reviewCount"))
                u.setReviewCount(node.get("reviewCount").asInt());
            if (node.has("emailVerified"))
                u.setEmailVerified(node.get("emailVerified").asBoolean());
            if (node.has("idVerified"))
                u.setIdVerified(node.get("idVerified").asBoolean());
            if (node.has("approved"))
                u.setApproved(node.get("approved").asBoolean()); // Jackson field name might trigger 'isApproved' getter
                                                                 // so 'approved' in JSON
            if (node.has("isApproved"))
                u.setApproved(node.get("isApproved").asBoolean());

            // DATA HEALING: If legacy 'isVerified' is true, ensure email and ID are also
            // verified
            // This fixes users who were verified before the granule flags were added/fixed
            if (u.isVerified()) {
                if (!u.isEmailVerified())
                    u.setEmailVerified(true);
                if (!u.isIdVerified())
                    u.setIdVerified(true);
                // We might also assume they are approved if they are verified, for non-admins?
                // Let's be safe and only touch verification flags.
                // Actually, for Sellers/Agents, verifyUser also sets approved=true.
                if (!u.isApproved() && !u.getRole().equalsIgnoreCase("admin")) {
                    u.setApproved(true);
                }
            }

            if (node.has("idDocumentPath") && !node.get("idDocumentPath").isNull())
                u.setIdDocumentPath(node.get("idDocumentPath").asText());
            if (node.has("avatarPath") && !node.get("avatarPath").isNull())
                u.setAvatarPath(node.get("avatarPath").asText());
            if (node.has("bio") && !node.get("bio").isNull())
                u.setBio(node.get("bio").asText());
            if (node.has("phoneNumber") && !node.get("phoneNumber").isNull())
                u.setPhoneNumber(node.get("phoneNumber").asText());
            if (node.has("city") && !node.get("city").isNull())
                u.setCity(node.get("city").asText());
            if (node.has("occupation") && !node.get("occupation").isNull())
                u.setOccupation(node.get("occupation").asText());
        }
        return u;
    }

    private void initializeDefaultUsers() {
        // Main Admin with fixed credentials (fully verified and approved)
        Admin mainAdmin = new Admin(1L, "Main Admin", "admin1@ds.gmail.com",
                securityService.hashPassword("admin1ds"), true, "MAIN_ADMIN_001");
        mainAdmin.setEmailVerified(true);
        mainAdmin.setIdVerified(true);
        mainAdmin.setApproved(true);
        users.add(mainAdmin);

        // Test users (verified and approved for testing)
        Buyer buyer = new Buyer(2L, "Buyer User", "buyer@example.com",
                securityService.hashPassword("buyer123"), true, "BUYER001");
        buyer.setEmailVerified(true);
        buyer.setIdVerified(true);
        buyer.setApproved(true);
        users.add(buyer);

        Seller seller = new Seller(3L, "Seller User", "seller@example.com",
                securityService.hashPassword("seller123"), true, "SELLER001");
        seller.setEmailVerified(true);
        seller.setIdVerified(true);
        seller.setApproved(true);
        users.add(seller);

        Agent agent = new Agent(4L, "Agent User", "agent@example.com",
                securityService.hashPassword("agent123"), true, "AGENT001");
        agent.setEmailVerified(true);
        agent.setIdVerified(true);
        agent.setApproved(true);
        users.add(agent);
    }

    public User login(String rawEmail, String password) {
        if (rawEmail == null)
            return null;
        String email = rawEmail.trim(); // TRIM INPUT
        // Emergency Fallback: Ensure Main Admin can always login
        if (email.equalsIgnoreCase("admin1@ds.gmail.com") && password.equals("admin1ds")) {
            System.out.println("Login successful (System Admin Override).");
            Admin admin = new Admin(1L, "Main Admin", "admin1@ds.gmail.com", "hashed_placeholder", true,
                    "MAIN_ADMIN_001");
            admin.setApproved(true);
            admin.setEmailVerified(true);
            admin.setIdVerified(true);
            return admin;
        }

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                // Verify password
                if (user.getPassword() == null || !securityService.verifyPassword(password, user.getPassword())) {
                    System.out.println("Login failed: Invalid credentials.");
                    throw new InvalidCredentialsException("Invalid credentials");
                }

                String role = user.getRole().toLowerCase();

                // Rule 1: Email must always be verified (skip for Main Admin override logic
                // handled above)
                if (!user.isEmailVerified() && !isMainAdmin(user)) {
                    System.out.println("Login failed: Email not verified.");
                    throw new EmailNotVerifiedException("Email not verified");
                }

                // Rule 2: Role-based checks
                if (role.equals("buyer")) {
                    // Buyer only needs email verification (checked above)
                    // No admin approval or ID verification required
                } else if (role.equals("admin")) {
                    // Admins must be verified/approved
                    if (!user.isVerified() && !isMainAdmin(user)) {
                        System.out.println("Login failed: Admin account not verified.");
                        throw new AccountPendingApprovalException("Admin account not verified");
                    }
                } else {
                    // Sellers and Agents
                    if (!user.isIdVerified()) {
                        System.out.println("Login failed: ID document not verified.");
                        throw new IdNotVerifiedException("ID document not verified");
                    }
                    if (!user.isApproved()) {
                        System.out.println("Login failed: Account pending admin approval.");
                        throw new AccountPendingApprovalException("Account pending admin approval");
                    }
                }

                System.out.println("Login successful for: " + user.getName());
                return user;
            }
        }
        System.out.println("Login failed: User not found.");
        throw new InvalidCredentialsException("Invalid credentials");
    }

    public User register(String rawName, String rawEmail, String password, String role, String govId) {
        String name = rawName != null ? rawName.trim() : "";
        String email = rawEmail != null ? rawEmail.trim() : "";

        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            System.out.println("Registration failed: Email already exists.");
            return null;
        }

        // Validate using SecurityService
        if (!securityService.validateEmail(email)) {
            System.out.println("Registration failed: Invalid email format.");
            return null;
        }

        if (!securityService.validatePassword(password)) {
            System.out.println("Registration failed: " + securityService.getPasswordRequirements());
            return null;
        }

        // Email domain validation for admins and regular users
        boolean isAdminEmail = email.toLowerCase().endsWith("@ds.gmail.com");
        boolean isAdminRole = role.equalsIgnoreCase("admin");

        if (isAdminRole && !isAdminEmail) {
            System.out.println("Registration failed: Admin accounts must use @ds.gmail.com email domain.");
            return null;
        }

        if (!isAdminRole && isAdminEmail) {
            System.out.println("Registration failed: @ds.gmail.com domain is reserved for administrators only.");
            return null;
        }

        Long newId = users.stream().mapToLong(User::getId).max().orElse(0) + 1;
        String hashed = securityService.hashPassword(password);

        boolean isVerified = false;

        User newUser = switch (role.toLowerCase()) {
            case "admin" -> new Admin(newId, name, email, hashed, isVerified, govId);
            case "buyer" -> new Buyer(newId, name, email, hashed, isVerified, govId);
            case "seller" -> new Seller(newId, name, email, hashed, isVerified, govId);
            case "agent" -> new Agent(newId, name, email, hashed, isVerified, govId);
            default -> null;
        };

        if (newUser != null) {
            // Apply Auto-Approval for Buyers
            if (role.equalsIgnoreCase("buyer")) {
                newUser.setApproved(true);
            }

            users.add(newUser);
            saveUsers();
            if (isAdminRole) {
                System.out.println(
                        "Admin registration successful! Please verify your email and wait for main admin approval.");
            } else if (role.equalsIgnoreCase("buyer")) {
                System.out.println("Registration successful! Please verify your email to log in.");
            } else {
                System.out.println("Registration successful! Please verify your email and upload ID document.");
            }
            return newUser;
        }
        return null;
    }

    public void verifyUser(Long userId) {
        Optional<User> userOpt = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setIdVerified(true); // Also verify ID if document was checked
            user.setEmailVerified(true); // FIX: Ensure email is marked verified when admin manually verifies
            user.setApproved(true); // Grant approval for login
            saveUsers();
            System.out.println("User verified: " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    public List<User> getUnverifiedUsers() {
        return users.stream().filter(u -> !u.isVerified()).collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Check if a user is the main admin
     */
    public boolean isMainAdmin(User user) {
        return user != null && user.getEmail().equalsIgnoreCase("admin1@ds.gmail.com");
    }

    /**
     * Approve a user (admin workflow)
     */
    public void approveUser(Long userId) {
        Optional<User> userOpt = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setApproved(true);
            user.setEmailVerified(true);
            user.setIdVerified(true);
            user.setVerified(true);
            saveUsers();
            System.out.println("User approved: " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Reject a user (admin workflow)
     */
    public void rejectUser(Long userId, String reason) {
        Optional<User> userOpt = users.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User rejected: " + user.getName() + " - Reason: " + reason);
            // Optionally delete the user
            users.remove(user);
            saveUsers();
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Get users pending approval
     */
    public List<User> getPendingApprovalUsers() {
        return users.stream()
                .filter(u -> !u.isApproved() && !isMainAdmin(u))
                .collect(Collectors.toList());
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all unverified admins (for main admin to review)
     */
    public List<User> getUnverifiedAdmins() {
        return users.stream()
                .filter(u -> u.getRole().equalsIgnoreCase("admin") && !u.isApproved())
                .collect(Collectors.toList());
    }

    public void updateUser(User user) {
        Optional<User> existing = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
        if (existing.isPresent()) {
            if (existing.get() != user) {
                int index = users.indexOf(existing.get());
                users.set(index, user);
            }
            saveUsers();
        }
    }
}
