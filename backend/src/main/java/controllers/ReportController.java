package controllers;

import dtos.ReportRequest;
import models.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ReportService;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ReportRequest request) {

        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Report report = reportService.createReport(
                    userId,
                    request.reportedUserId(),
                    request.reportedPropertyId(),
                    request.reason(),
                    request.description());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create report: " + e.getMessage());
        }
    }
}
