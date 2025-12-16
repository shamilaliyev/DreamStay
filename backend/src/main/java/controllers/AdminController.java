package controllers;

import dtos.*;
import models.User;
import models.Report;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.ReportService;
import services.VerificationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AuthService authService;
    private final ReportService reportService;
    private final VerificationService verificationService;
    private final services.PropertyManager propertyManager;
    private final DtoMapper dtoMapper;

    // ReportService and VerificationService injected
    public AdminController(AuthService authService, ReportService reportService,
            VerificationService verificationService, services.PropertyManager propertyManager, DtoMapper dtoMapper) {
        this.authService = authService;
        this.reportService = reportService;
        this.verificationService = verificationService;
        this.propertyManager = propertyManager;
        this.dtoMapper = dtoMapper;
    }

    private boolean isAdmin(Long userId) {
        if (userId == null)
            return false;
        User user = authService.getUserById(userId);
        return user != null && user.getRole().equalsIgnoreCase("admin");
    }

    @GetMapping("/users/pending")
    public ResponseEntity<?> getPendingUsers(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<User> pending = authService.getPendingApprovalUsers();
        return ResponseEntity.ok(pending.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    // Retain old approve-user for generic approval, or assume verify covers it on
    // the "unverified" list.
    // However, strict requirement asks for verify on user ID.
    @PostMapping("/users/{id}/verify")
    public ResponseEntity<?> verifyUser(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // This endpoint maps to the ID verification approval
        User target = authService.getUserById(id);
        if (target != null) {
            target.setIdVerified(true);
            target.setApproved(true);
            authService.updateUser(target);
        }

        verificationService.verifyUser(id);
        return ResponseEntity.ok("User ID verified and approved");
    }

    // START NEW ADMIN ENDPOINTS

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<User> users = authService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    @GetMapping("/users/id-pending")
    public ResponseEntity<?> getPendingIdUsers(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<User> users = authService.getAllUsers().stream()
                .filter(u -> u.getIdDocumentPath() != null && !u.isIdVerified())
                .collect(Collectors.toList());

        return ResponseEntity.ok(users.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = authService.getUserById(id);
        if (user == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(dtoMapper.toUserDTO(user));
    }

    @GetMapping("/users/unverified")
    public ResponseEntity<?> getUnverifiedUsers(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<User> users = verificationService.getUnverifiedUsers();
        return ResponseEntity.ok(users.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<?> rejectUser(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User target = authService.getUserById(id);
        if (target == null)
            return ResponseEntity.notFound().build();

        if (authService.isMainAdmin(target)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot reject Main Admin");
        }

        authService.rejectUser(id, "Admin Rejected via API");
        return ResponseEntity.ok("User rejected");
    }

    @GetMapping("/admins/unverified")
    public ResponseEntity<?> getUnverifiedAdmins(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<User> admins = verificationService.getUnverifiedAdmins();
        return ResponseEntity.ok(admins.stream().map(dtoMapper::toUserDTO).collect(Collectors.toList()));
    }

    @PostMapping("/admins/{id}/verify")
    public ResponseEntity<?> verifyAdmin(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        verificationService.verifyAdmin(id);
        return ResponseEntity.ok("Admin verified");
    }

    @GetMapping("/properties")
    public ResponseEntity<?> getAllProperties(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<models.Property> properties = propertyManager.getProperties();
        return ResponseEntity.ok(properties.stream().map(dtoMapper::toPropertyDTO).collect(Collectors.toList()));
    }

    @GetMapping("/properties/unverified")
    public ResponseEntity<?> getUnverifiedProperties(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<models.Property> properties = propertyManager.getUnverifiedProperties();

        List<AdminUnverifiedPropertyDTO> dtos = properties.stream().map(p -> {
            User owner = authService.getUserById(p.getOwnerId());
            return dtoMapper.toAdminUnverifiedPropertyDTO(p, owner);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/users/{id}/id-document")
    public ResponseEntity<?> getUserIdDocument(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long id) {

        if (!isAdmin(adminId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = authService.getUserById(id);
        if (user == null || user.getIdDocumentPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(user.getIdDocumentPath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg") // Simple assumption or
                                                                                                 // detect
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (java.net.MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<?> getPropertyDetails(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        java.util.Optional<models.Property> propertyOpt = propertyManager.getPropertyById(id);
        if (propertyOpt.isEmpty())
            return ResponseEntity.notFound().build();

        models.Property property = propertyOpt.get();
        User owner = authService.getUserById(property.getOwnerId());

        return ResponseEntity.ok(dtoMapper.toAdminUnverifiedPropertyDTO(property, owner));
    }

    @PostMapping("/properties/{id}/verify")
    public ResponseEntity<?> verifyProperty(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        propertyManager.verifyProperty(id);
        return ResponseEntity.ok("Property verified");
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<?> deleteProperty(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        propertyManager.deleteProperty(id);
        return ResponseEntity.ok("Property deleted");
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<?> getPendingReports(@RequestHeader("X-User-Id") Long userId) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<Report> reports = reportService.getPendingReports();
        return ResponseEntity.ok(reports.stream().map(dtoMapper::toReportDTO).collect(Collectors.toList()));
    }

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        reportService.resolveReport(id, "Resolved via API");
        return ResponseEntity.ok("Report resolved");
    }

    @PostMapping("/reports/{id}/dismiss")
    public ResponseEntity<?> dismissReport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        if (!isAdmin(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        reportService.dismissReport(id, "Dismissed via API");
        return ResponseEntity.ok("Report dismissed");
    }
}
