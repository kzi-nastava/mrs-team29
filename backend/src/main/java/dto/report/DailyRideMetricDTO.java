package dto.report;

import java.time.LocalDate;

public class DailyRideMetricDTO {
    private LocalDate date;
    private int ridesCount;
    private double kilometers;
    private double amount;

    public DailyRideMetricDTO() {
    }

    public DailyRideMetricDTO(LocalDate date, int ridesCount, double kilometers, double amount) {
        this.date = date;
        this.ridesCount = ridesCount;
        this.kilometers = kilometers;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getRidesCount() {
        return ridesCount;
    }

    public void setRidesCount(int ridesCount) {
        this.ridesCount = ridesCount;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
