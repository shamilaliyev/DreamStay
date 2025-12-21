package services;

/**
 * author @frhetto Nijat Alisoy
 * This class authenticates the users stored in the PostgreSQL database.
 * User Management: Stores and handles User objects.
 * Data Persistence: Reads and writes user data to the database via UserRepository.
 * Login: Authenticates users by email.
 * Default Users: Initializes with default users if no data exists.
 * Error Handling: Handles data-related issues gracefully.
 * 
 */

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import exceptions.AccountPendingApprovalException;
import exceptions.EmailNotVerifiedException;
import exceptions.IdNotVerifiedException;
import exceptions.InvalidCredentialsException;
import models.Admin;
import models.Agent;
import models.Buyer;
import models.Seller;
import models.User;
import repositories.UserRepository;

public class AuthService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public AuthService(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        ensureDefaultAdminExists();
    }

    private void ensureDefaultAdminExists() {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("admin"));
        if (!adminExists) {
            Admin mainAdmin = new Admin(null, "Main Admin", "admin1@ds.gmail.com",
                    securityService.hashPassword("admin1ds"), true, "MAIN_ADMIN_001");
            mainAdmin.setEmailVerified(true);
            mainAdmin.setIdVerified(true);
            mainAdmin.setApproved(true);
            userRepository.save(mainAdmin);

            // Optional: seed a couple of demo users if desired
            Buyer buyer = new Buyer(null, "Buyer User", "buyer@example.com",
                    securityService.hashPassword("buyer123"), true, "BUYER001");
            buyer.setEmailVerified(true);
            buyer.setIdVerified(true);
            buyer.setApproved(true);
            userRepository.save(buyer);

            Seller seller = new Seller(null, "Seller User", "seller@example.com",
                    securityService.hashPassword("seller123"), true, "SELLER001");
            seller.setEmailVerified(true);
            seller.setIdVerified(true);
            seller.setApproved(true);
            userRepository.save(seller);

            Agent agent = new Agent(null, "Agent User", "agent@example.com",
                    securityService.hashPassword("agent123"), true, "AGENT001");
            agent.setEmailVerified(true);
            agent.setIdVerified(true);
            agent.setApproved(true);
            userRepository.save(agent);
        }
    }

    public User login(String rawEmail, String password) {
        if (rawEmail == null)
            return null;
        String email = rawEmail.trim(); // TRIM INPUT
        System.out.println("DEBUG: Login attempt for email: '" + email + "'");
        System.out.println("DEBUG: Total users in system (DB): " + userRepository.count());
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

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // DEBUG: Log user found and password check
            System.out.println("DEBUG: User found: " + user.getEmail() + ", Role: " + user.getRole());
            System.out.println("DEBUG: Password hash in DB: " + (user.getPassword() != null
                    ? user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "..."
                    : "NULL"));
            System.out.println("DEBUG: Email verified: " + user.isEmailVerified() + ", ID verified: "
                    + user.isIdVerified() + ", Approved: " + user.isApproved());

            // Verify password
            if (user.getPassword() == null) {
                System.out.println("DEBUG: Login failed: User password is NULL");
                throw new InvalidCredentialsException("Invalid credentials");
            }

            boolean passwordMatch = securityService.verifyPassword(password, user.getPassword());
            System.out.println("DEBUG: Password verification result: " + passwordMatch);

            if (!passwordMatch) {
                System.out.println("DEBUG: Login failed: Password mismatch");
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
        System.out.println("Login failed: User not found.");
        throw new InvalidCredentialsException("Invalid credentials");
    }

    public User register(String rawName, String rawEmail, String password, String role, String govId) {
        String name = rawName != null ? rawName.trim() : "";
        String email = rawEmail != null ? rawEmail.trim() : "";

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
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

        String hashed = securityService.hashPassword(password);

        boolean isVerified = false;

        User newUser = switch (role.toLowerCase()) {
            case "admin" -> new Admin(null, name, email, hashed, isVerified, govId);
            case "buyer" -> new Buyer(null, name, email, hashed, isVerified, govId);
            case "seller" -> new Seller(null, name, email, hashed, isVerified, govId);
            case "agent" -> new Agent(null, name, email, hashed, isVerified, govId);
            default -> null;
        };

        if (newUser != null) {
            // Apply Auto-Approval for Buyers
            if (role.equalsIgnoreCase("buyer")) {
                newUser.setApproved(true);
            }

            newUser = userRepository.save(newUser);
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setIdVerified(true); // Also verify ID if document was checked
            user.setEmailVerified(true); // FIX: Ensure email is marked verified when admin manually verifies
            user.setApproved(true); // Grant approval for login
            userRepository.save(user);
            System.out.println("User verified: " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    public List<User> getUnverifiedUsers() {
        return userRepository.findAll().stream().filter(u -> !u.isVerified()).collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setApproved(true);
            user.setEmailVerified(true);
            user.setIdVerified(true);
            user.setVerified(true);
            userRepository.save(user);
            System.out.println("User approved: " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Reject a user (admin workflow)
     */
    public void rejectUser(Long userId, String reason) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User rejected: " + user.getName() + " - Reason: " + reason);
            // Optionally delete the user
            userRepository.delete(user);
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Get users pending approval
     */
    public List<User> getPendingApprovalUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isApproved() && !isMainAdmin(u))
                .collect(Collectors.toList());
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all unverified admins (for main admin to review)
     */
    public List<User> getUnverifiedAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase("admin") && !u.isApproved())
                .collect(Collectors.toList());
    }

    public void updateUser(User user) {
        // JPA will handle insert/update based on the entity ID
        userRepository.save(user);
        System.out.println("DEBUG: User updated and saved: " + user.getEmail());
    }
}
