package dto.ride;

import jakarta.validation.constraints.NotBlank;

public class InconsistencyNoteDTO {
    
    @NotBlank(message = "Ride ID is required")
    private String rideId;
    
    @NotBlank(message = "Note text is required")
    private String noteText;
    
    public InconsistencyNoteDTO() {}
    
    public InconsistencyNoteDTO(String rideId, String noteText) {
        this.rideId = rideId;
        this.noteText = noteText;
    }
    
    public String getRideId() { return rideId; }
    public String getNoteText() { return noteText; }
    
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
}
