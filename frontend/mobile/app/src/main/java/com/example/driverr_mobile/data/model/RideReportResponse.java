package com.example.driverr_mobile.data.model;

import java.util.List;

public class RideReportResponse {
    private String startDate;
    private String endDate;
    private List<DailyRideMetric> daily;

    private int totalRides;
    private double totalKilometers;
    private double totalAmount;

    private double averageRidesPerDay;
    private double averageKilometersPerDay;
    private double averageAmountPerDay;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<DailyRideMetric> getDaily() {
        return daily;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public double getTotalKilometers() {
        return totalKilometers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAverageRidesPerDay() {
        return averageRidesPerDay;
    }

    public double getAverageKilometersPerDay() {
        return averageKilometersPerDay;
    }

    public double getAverageAmountPerDay() {
        return averageAmountPerDay;
    }
}
