package dto.map;

public class RouteResultDTO {

    private double distanceMeters;
    private int durationSeconds;

    public RouteResultDTO() {}

    public RouteResultDTO(double distanceMeters, int durationSeconds) {
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
    }

    public double getDistanceMeters() { return distanceMeters; }
    public int getDurationSeconds() { return durationSeconds; }

    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}
