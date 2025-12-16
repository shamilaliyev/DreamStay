package controllers;

import dtos.*;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.EmailVerificationService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final DtoMapper dtoMapper;
    private final services.FileUploadService fileUploadService;

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService,
            DtoMapper dtoMapper, services.FileUploadService fileUploadService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
        this.dtoMapper = dtoMapper;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.email(), request.password());
            return ResponseEntity.ok(dtoMapper.toUserDTO(user));
        } catch (exceptions.InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (exceptions.EmailNotVerifiedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (exceptions.IdNotVerifiedException | exceptions.AccountPendingApprovalException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("governmentId") String governmentId,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {
        try {
            User newUser = authService.register(
                    name,
                    email,
                    password,
                    role,
                    governmentId);

            if (newUser != null) {
                // If ID file is provided, save it
                if (file != null && !file.isEmpty()) {
                    String path = fileUploadService.saveIdDocument(newUser.getId(), file.getBytes(),
                            file.getOriginalFilename());
                    newUser.setIdDocumentPath(path);

                    // Reset status to ensure it's pending review
                    newUser.setIdVerified(false);
                    newUser.setApproved(false);

                    authService.updateUser(newUser);
                } else if (role.equalsIgnoreCase("seller") || role.equalsIgnoreCase("agent")) {
                    // If no file but required role, strictly mark unverified (handled by defaults,
                    // but good to be explicit)
                    // Ideally frontend forces file, but backend should handle missing file
                    // appropriately
                }

                emailVerificationService.sendVerificationCode(newUser);
                return ResponseEntity.ok("User registered successfully. check email for verification code.");
            } else {
                return ResponseEntity.badRequest().body("Registration failed. Email might be in use or invalid data.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        User user = authService.getUserByEmail(request.email());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        boolean verified = emailVerificationService.verifyCode(user, request.code());
        if (verified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification code");
        }
    }
}
