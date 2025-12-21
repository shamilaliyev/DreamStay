package services;

import models.Report;
import repositories.ReportRepository;
import java.util.List;

/**
 * Service for managing user and property reports (FR-09, FR-10, FR-11)
 */
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Long reporterId, Long reportedUserId, Long reportedPropertyId,
            String reason, String description) {
        Report report = new Report(null, reporterId, reportedUserId, reportedPropertyId, reason, description);
        return reportRepository.save(report);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus("PENDING");
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    public void resolveReport(Long reportId, String adminNotes) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("RESOLVED");
            report.setAdminNotes(adminNotes);
            reportRepository.save(report);
        }
    }

    public void dismissReport(Long reportId, String adminNotes) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("DISMISSED");
            report.setAdminNotes(adminNotes);
            reportRepository.save(report);
        }
    }

    public void markAsReviewed(Long reportId) {
        Report report = getReportById(reportId);
        if (report != null) {
            report.setStatus("REVIEWED");
            reportRepository.save(report);
        }
    }

    public List<Report> getReportsForUser(Long userId) {
        return reportRepository.findByReportedUserId(userId);
    }

    public List<Report> getReportsForProperty(Long propertyId) {
        return reportRepository.findByReportedPropertyId(propertyId);
    }
}
