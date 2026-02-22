package domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_inconsistency_note")
public class DriverInconsistencyNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotNull(message = "Ride cannot be null")
    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;
    
    @NotNull(message = "Reporting passenger cannot be null")
    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;
    
    @NotBlank(message = "Note text cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String noteText;
    
    @NotNull(message = "Timestamp cannot be null")
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    public DriverInconsistencyNote() {
        this.timestamp = LocalDateTime.now();
    }
    
    public DriverInconsistencyNote(Ride ride, User passenger, String noteText) {
        this.ride = ride;
        this.passenger = passenger;
        this.noteText = noteText;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public Ride getRide() { return ride; }
    public User getPassenger() { return passenger; }
    public String getNoteText() { return noteText; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public void setId(String id) { this.id = id; }
    public void setRide(Ride ride) { this.ride = ride; }
    public void setPassenger(User passenger) { this.passenger = passenger; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
