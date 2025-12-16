package controllers;

import dtos.DtoMapper;
import dtos.UserDTO;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.AuthService;
import services.FileUploadService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;
    private final DtoMapper dtoMapper;

    public ProfileController(AuthService authService, FileUploadService fileUploadService, DtoMapper dtoMapper) {
        this.authService = authService;
        this.fileUploadService = fileUploadService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> updates) {

        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Apply updates (validate fields as needed)
        if (updates.containsKey("bio"))
            user.setBio(updates.get("bio"));
        if (updates.containsKey("phoneNumber"))
            user.setPhoneNumber(updates.get("phoneNumber"));
        if (updates.containsKey("city"))
            user.setCity(updates.get("city"));
        if (updates.containsKey("occupation"))
            user.setOccupation(updates.get("occupation"));

        // Name is usually editable, but Role/Email are immutable
        if (updates.containsKey("name"))
            user.setName(updates.get("name"));

        authService.updateUser(user);

        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) {

        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Delete old avatar if exists? (Optional cleanup)
            // if (user.getAvatarPath() != null)
            // fileUploadService.deleteFile(user.getAvatarPath());

            String path = fileUploadService.saveAvatar(userId, file.getBytes(), file.getOriginalFilename());
            user.setAvatarPath(path);
            authService.updateUser(user);

            return ResponseEntity.ok(Map.of("avatarUrl", fileUploadService.getPublicUrl(path)));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}
