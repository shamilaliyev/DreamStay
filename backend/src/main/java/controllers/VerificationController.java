package controllers;

import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.AuthService;
import services.FileUploadService;
import services.VerificationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    public VerificationController(AuthService authService, FileUploadService fileUploadService) {
        this.authService = authService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload-id")
    public ResponseEntity<?> uploadId(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) {

        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Only Sellers and Agents need verification
        String role = user.getRole().toLowerCase();
        if (!role.equals("seller") && !role.equals("agent")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Buyers do not need ID verification");
        }

        try {
            String path = fileUploadService.saveIdDocument(userId, file.getBytes(), file.getOriginalFilename());

            user.setIdDocumentPath(path);
            user.setIdVerified(false); // Reset to pending if re-uploaded
            user.setApproved(false); // Reset approval

            authService.updateUser(user);

            return ResponseEntity.ok("ID uploaded successfully. Pending admin approval.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}
