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
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            parseJson(json.toString());
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(toJson());
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void parseJson(String json) {
        if (json == null || json.trim().isEmpty() || json.equals("[]"))
            return;

        // Simple manual JSON array parser
        // Assumes objects are separated by "},{" inside the array brackets
        String inner = json.trim();
        if (inner.startsWith("["))
            inner = inner.substring(1);
        if (inner.endsWith("]"))
            inner = inner.substring(0, inner.length() - 1);

        List<String> userStrings = splitJsonObjects(inner);

        users.clear();
        for (String userString : userStrings) {
            User user = parseUser(userString);
            if (user != null) {
                users.add(user);
            }
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
                // skip comma between objects
                current = new StringBuilder();
            }
        }
        return list;
    }

    private String toJson() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            json.append(userToJson(user));
            if (i < users.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String userToJson(User user) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"password\":\"%s\",\"isVerified\":%b,\"emailVerified\":%b,\"idVerified\":%b,\"isApproved\":%b,\"governmentId\":\"%s\",\"idDocumentPath\":\"%s\",\"avatarPath\":\"%s\",\"bio\":\"%s\",\"phoneNumber\":\"%s\",\"city\":\"%s\",\"occupation\":\"%s\",\"averageRating\":%.2f,\"reviewCount\":%d}",
                user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getPassword(), user.isVerified(),
                user.isEmailVerified(), user.isIdVerified(), user.isApproved(),
                user.getGovernmentId() == null ? "null" : user.getGovernmentId(),
                user.getIdDocumentPath() == null ? "null" : user.getIdDocumentPath(),
                user.getAvatarPath() == null ? "null" : user.getAvatarPath(),
                user.getBio() == null ? "null" : user.getBio(),
                user.getPhoneNumber() == null ? "null" : user.getPhoneNumber(),
                user.getCity() == null ? "null" : user.getCity(),
                user.getOccupation() == null ? "null" : user.getOccupation(),
                user.getAverageRating(), user.getReviewCount());
    }

    private User parseUser(String json) {
        Map<String, String> fields = parseJsonFields(json);
        Long id = null;
        String name = null, email = null, role = null, password = null, govId = null;
        boolean isVerified = false;

        try {
            id = Long.parseLong(fields.getOrDefault("id", "0"));
            name = fields.getOrDefault("name", "");
            email = fields.getOrDefault("email", "");
            role = fields.getOrDefault("role", "");
            password = fields.getOrDefault("password", "");
            isVerified = Boolean.parseBoolean(fields.getOrDefault("isVerified", "false"));
            govId = fields.getOrDefault("governmentId", "null");
            if (govId.equals("null"))
                govId = null;
            double rating = Double.parseDouble(fields.getOrDefault("averageRating",
                    fields.getOrDefault("rating", "0.0")));
            int reviewCount = Integer.parseInt(fields.getOrDefault("reviewCount", "0"));

            User u = switch (role.toLowerCase()) {
                case "admin" -> new Admin(id, name, email, password, isVerified, govId);
                case "buyer" -> new Buyer(id, name, email, password, isVerified, govId);
                case "seller" -> new Seller(id, name, email, password, isVerified, govId);
                case "agent" -> new Agent(id, name, email, password, isVerified, govId);
                default -> null;
            };
            if (u != null) {
                u.setAverageRating(rating);
                u.setReviewCount(reviewCount);
                // Load additional fields if present
                u.setEmailVerified(Boolean.parseBoolean(fields.getOrDefault("emailVerified", "false")));
                u.setIdVerified(Boolean.parseBoolean(fields.getOrDefault("idVerified", "false")));
                u.setApproved(Boolean.parseBoolean(fields.getOrDefault("isApproved", "false")));
                String idDocPath = fields.getOrDefault("idDocumentPath", "null");
                if (!idDocPath.equals("null")) {
                    u.setIdDocumentPath(idDocPath);
                }
                String avatar = fields.getOrDefault("avatarPath", "null");
                if (!avatar.equals("null"))
                    u.setAvatarPath(avatar);
                String bio = fields.getOrDefault("bio", "null");
                if (!bio.equals("null"))
                    u.setBio(bio);
                String phone = fields.getOrDefault("phoneNumber", "null");
                if (!phone.equals("null"))
                    u.setPhoneNumber(phone);
                String city = fields.getOrDefault("city", "null");
                if (!city.equals("null"))
                    u.setCity(city);
                String occ = fields.getOrDefault("occupation", "null");
                if (!occ.equals("null"))
                    u.setOccupation(occ);
            }
            return u;
        } catch (Exception e) {
            System.out.println("Error parsing user fields: " + e.getMessage());
            return null;
        }
    }

    private Map<String, String> parseJsonFields(String json) {
        Map<String, String> fields = new HashMap<>();
        // Remove braces
        String content = json.trim();
        if (content.startsWith("{"))
            content = content.substring(1);
        if (content.endsWith("}"))
            content = content.substring(0, content.length() - 1);

        // Split by comma, but be careful about commas in values?
        // For this prototype, we assume no commas in values or handle simple cases.
        // A regex split is better.
        String[] pairs = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                fields.put(key, value);
            }
        }
        return fields;
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

    public User login(String email, String password) {
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
                    return null;
                }

                String role = user.getRole().toLowerCase();

                // Rule 1: Email must always be verified (skip for Main Admin override logic
                // handled above)
                if (!user.isEmailVerified() && !isMainAdmin(user)) {
                    System.out.println("Login failed: Email not verified.");
                    return null;
                }

                // Rule 2: Role-based checks
                if (role.equals("buyer")) {
                    // Buyer only needs email verification (checked above)
                    // No admin approval or ID verification required
                } else if (role.equals("admin")) {
                    // Admins must be verified/approved
                    if (!user.isVerified() && !isMainAdmin(user)) {
                        System.out.println("Login failed: Admin account not verified.");
                        return null;
                    }
                } else {
                    // Sellers and Agents
                    if (!user.isIdVerified()) {
                        System.out.println("Login failed: ID document not verified.");
                        return null;
                    }
                    if (!user.isApproved()) {
                        System.out.println("Login failed: Account pending admin approval.");
                        return null;
                    }
                }

                System.out.println("Login successful for: " + user.getName());
                return user;
            }
        }
        System.out.println("Login failed: Invalid credentials.");
        return null;
    }

    public User register(String name, String email, String password, String role, String govId) {
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
