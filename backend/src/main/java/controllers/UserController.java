package controllers;

import dtos.DtoMapper;
import dtos.UserDTO;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.AuthService;
import services.IDUploadService;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthService authService;
    private final IDUploadService idUploadService;
    private final DtoMapper dtoMapper;

    public UserController(AuthService authService, IDUploadService idUploadService, DtoMapper dtoMapper) {
        this.authService = authService;
        this.idUploadService = idUploadService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        User user = authService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Use PublicUserDTO to protect sensitive data
        return ResponseEntity.ok(dtoMapper.toPublicUserDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<?> findUserByEmail(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String email) {

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User currentUser = authService.getUserById(userId);
        if (currentUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User match = authService.getUserByEmail(email);
        if (match == null) {
            // Fallback: try search by name if email failed
            java.util.List<User> nameMatches = authService.getAllUsers().stream()
                    .filter(u -> u.getName().equalsIgnoreCase(email))
                    .collect(java.util.stream.Collectors.toList());

            if (!nameMatches.isEmpty()) {
                // If duplicates, take first or handle logic. Taking first for now.
                match = nameMatches.get(0);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }

        if (match.getId().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot chat with yourself");
        }

        if (!match.isApproved() && !authService.isMainAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not verified/approved yet");
        }

        return ResponseEntity.ok(dtoMapper.toUserDTO(match));
    }

    @PostMapping("/{id}/upload-id")
    public ResponseEntity<?> uploadIdDocument(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        if (!userId.equals(id)) { // basic check
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot upload for another user");
        }

        User user = authService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String path = idUploadService.uploadIDDocument(id, file.getBytes(), file.getOriginalFilename());
            user.setIdDocumentPath(path);
            authService.saveUsers(); // Make sure to save the updated path
            return ResponseEntity.ok("ID Document uploaded: " + path);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}
