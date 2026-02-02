package domain.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private int driverRating;    // 1–5
    
    @Column(nullable = false)
    private int vehicleRating;   // 1–5
    
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "ride_finished_at")
    private LocalDateTime rideFinishedAt;
    
    @Column(name = "rated_at")
    private LocalDateTime ratedAt;

    private boolean expired;

    public Rating() {}

    public Rating(Ride ride, User passenger, Driver driver, Vehicle vehicle,
                  int driverRating, int vehicleRating, String comment,
                  LocalDateTime rideFinishedAt, LocalDateTime ratedAt, boolean expired) {
        this.ride = ride;
        this.passenger = passenger;
        this.driver = driver;
        this.vehicle = vehicle;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.rideFinishedAt = rideFinishedAt;
        this.ratedAt = ratedAt;
        this.expired = expired;
    }

    public String getId() { return id; }
    public Ride getRide() { return ride; }
    public User getPassenger() { return passenger; }
    public Driver getDriver() { return driver; }
    public Vehicle getVehicle() { return vehicle; }
    public int getDriverRating() { return driverRating; }
    public int getVehicleRating() { return vehicleRating; }
    public String getComment() { return comment; }
    public LocalDateTime getRideFinishedAt() { return rideFinishedAt; }
    public LocalDateTime getRatedAt() { return ratedAt; }
    public boolean isExpired() { return expired; }

    public void setId(String id) { this.id = id; }
    public void setRide(Ride ride) { this.ride = ride; }
    public void setPassenger(User passenger) { this.passenger = passenger; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setDriverRating(int driverRating) { this.driverRating = driverRating; }
    public void setVehicleRating(int vehicleRating) { this.vehicleRating = vehicleRating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRideFinishedAt(LocalDateTime rideFinishedAt) { this.rideFinishedAt = rideFinishedAt; }
    public void setRatedAt(LocalDateTime ratedAt) { this.ratedAt = ratedAt; }
    public void setExpired(boolean expired) { this.expired = expired; }
}

