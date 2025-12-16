package services;

import models.Report;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user and property reports (FR-09, FR-10, FR-11)
 */
public class ReportService {
    private static final String FILE_PATH = "reports.json";
    private List<Report> reports;
    private long nextId;

    public ReportService() {
        this.reports = new ArrayList<>();
        this.nextId = 1;
        loadReports();
    }

    /**
     * Create a new report
     */
    public Report createReport(Long reporterId, Long reportedUserId, Long reportedPropertyId,
            String reason, String description) {
        Report report = new Report(nextId++, reporterId, reportedUserId, reportedPropertyId, reason, description);
        reports.add(report);
        saveReports();
        System.out.println("Report created successfully. ID: " + report.getId());
        return report;
    }

    /**
     * Get all pending reports
     */
    public List<Report> getPendingReports() {
        return reports.stream()
                .filter(r -> r.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
    }

    /**
     * Get all reports
     */
    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }

    /**
     * Get report by ID
     */
    public Report getReportById(Long id) {
        return reports.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Resolve a report
     */
    public void resolveReport(Long reportId, String adminNotes) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("RESOLVED");
            report.setAdminNotes(adminNotes);
            saveReports();
            System.out.println("Report #" + reportId + " resolved.");
        } else {
            System.out.println("Report not found.");
        }
    }

    /**
     * Dismiss a report
     */
    public void dismissReport(Long reportId, String adminNotes) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("DISMISSED");
            report.setAdminNotes(adminNotes);
            saveReports();
            System.out.println("Report #" + reportId + " dismissed.");
        } else {
            System.out.println("Report not found.");
        }
    }

    /**
     * Mark report as reviewed
     */
    public void markAsReviewed(Long reportId) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("REVIEWED");
            saveReports();
        }
    }

    /**
     * Get reports for a specific user
     */
    public List<Report> getReportsForUser(Long userId) {
        return reports.stream()
                .filter(r -> userId.equals(r.getReportedUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Get reports for a specific property
     */
    public List<Report> getReportsForProperty(Long propertyId) {
        return reports.stream()
                .filter(r -> propertyId.equals(r.getReportedPropertyId()))
                .collect(Collectors.toList());
    }

    // Persistence methods
    private void loadReports() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            parseJson(sb.toString());
        } catch (IOException e) {
            System.err.println("Error loading reports: " + e.getMessage());
        }
    }

    private void saveReports() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(toJson());
        } catch (IOException e) {
            System.err.println("Error saving reports: " + e.getMessage());
        }
    }

    private void parseJson(String json) {
        // Simple JSON parsing - for prototype
        // In production, use a JSON library
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return;
        }
        // Placeholder - implement if needed for persistence
    }

    private String toJson() {
        // Simple JSON serialization
        return "[]"; // Placeholder for prototype
    }
}
