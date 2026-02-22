package dto.report;

import java.time.LocalDate;
import java.util.List;

public class RideReportResponseDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyRideMetricDTO> daily;

    private int totalRides;
    private double totalKilometers;
    private double totalAmount;

    private double averageRidesPerDay;
    private double averageKilometersPerDay;
    private double averageAmountPerDay;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<DailyRideMetricDTO> getDaily() {
        return daily;
    }

    public void setDaily(List<DailyRideMetricDTO> daily) {
        this.daily = daily;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public double getTotalKilometers() {
        return totalKilometers;
    }

    public void setTotalKilometers(double totalKilometers) {
        this.totalKilometers = totalKilometers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAverageRidesPerDay() {
        return averageRidesPerDay;
    }

    public void setAverageRidesPerDay(double averageRidesPerDay) {
        this.averageRidesPerDay = averageRidesPerDay;
    }

    public double getAverageKilometersPerDay() {
        return averageKilometersPerDay;
    }

    public void setAverageKilometersPerDay(double averageKilometersPerDay) {
        this.averageKilometersPerDay = averageKilometersPerDay;
    }

    public double getAverageAmountPerDay() {
        return averageAmountPerDay;
    }

    public void setAverageAmountPerDay(double averageAmountPerDay) {
        this.averageAmountPerDay = averageAmountPerDay;
    }
}
