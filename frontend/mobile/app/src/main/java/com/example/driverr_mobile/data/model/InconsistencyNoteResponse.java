package com.example.driverr_mobile.data.model;

public class InconsistencyNoteResponse {
    private String id;
    private String rideId;
    private String passengerId;
    private String passengerName;
    private String noteText;
    private String timestamp;

    public InconsistencyNoteResponse() {}

    public String getId() { return id; }
    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public String getPassengerName() { return passengerName; }
    public String getNoteText() { return noteText; }
    public String getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
