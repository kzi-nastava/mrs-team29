package dto.rating;

public class RatingRequestDTO {

    private String rideId;
    private String passengerId;

    private int driverRating;   // 1–5
    private int vehicleRating;  // 1–5
    private String comment;

    public RatingRequestDTO() {}

    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public int getDriverRating() { return driverRating; }
    public int getVehicleRating() { return vehicleRating; }
    public String getComment() { return comment; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setDriverRating(int driverRating) { this.driverRating = driverRating; }
    public void setVehicleRating(int vehicleRating) { this.vehicleRating = vehicleRating; }
    public void setComment(String comment) { this.comment = comment; }
}
