package dto.ride;

public class RideInconsistencyReportDTO {

    private String rideId;
    private String passengerId;
    private String note;

    public RideInconsistencyReportDTO() {}

    public RideInconsistencyReportDTO(String rideId,
                                      String passengerId,
                                      String note) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.note = note;
    }

    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public String getNote() { return note; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setNote(String note) { this.note = note; }
}
