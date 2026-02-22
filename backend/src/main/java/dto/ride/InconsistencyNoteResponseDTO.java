package dto.ride;

import java.time.LocalDateTime;

public class InconsistencyNoteResponseDTO {
    
    private String id;
    private String rideId;
    private String passengerId;
    private String passengerName;
    private String noteText;
    private LocalDateTime timestamp;
    
    public InconsistencyNoteResponseDTO() {}
    
    public InconsistencyNoteResponseDTO(String id, String rideId, String passengerId, 
                                       String passengerName, String noteText, LocalDateTime timestamp) {
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.noteText = noteText;
        this.timestamp = timestamp;
    }
    
    public String getId() { return id; }
    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public String getPassengerName() { return passengerName; }
    public String getNoteText() { return noteText; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public void setId(String id) { this.id = id; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
