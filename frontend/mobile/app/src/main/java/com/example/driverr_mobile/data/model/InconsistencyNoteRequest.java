package com.example.driverr_mobile.data.model;

public class InconsistencyNoteRequest {
    private String rideId;
    private String noteText;

    public InconsistencyNoteRequest() {}

    public InconsistencyNoteRequest(String rideId, String noteText) {
        this.rideId = rideId;
        this.noteText = noteText;
    }

    public String getRideId() { return rideId; }
    public String getNoteText() { return noteText; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
}
