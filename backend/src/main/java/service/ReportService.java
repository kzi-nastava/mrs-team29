package service;

import java.time.LocalDate;

import domain.enums.UserType;
import dto.report.RideReportResponseDTO;

public interface ReportService {
    RideReportResponseDTO getUserRideReport(String userId, LocalDate startDate, LocalDate endDate);

    RideReportResponseDTO getAdminRideReport(
            UserType scope,
            String userId,
            String userEmail,
            LocalDate startDate,
            LocalDate endDate
    );
}
