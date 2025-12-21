package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Report;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Find all reports with specific status (e.g. PENDING)
    List<Report> findByStatus(String status);

    // Find reports against a specific user
    List<Report> findByReportedUserId(Long reportedUserId);

    // Find reports against a specific property
    List<Report> findByReportedPropertyId(Long reportedPropertyId);
}
