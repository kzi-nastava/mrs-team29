package dto.driver;

import domain.entities.Driver;
import domain.entities.Vehicle;

public class DriverDetailsDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private boolean active;
    private boolean blocked;

    private String vehicleModel;
    private String vehicleType;
    private String registrationPlate;

    public static DriverDetailsDTO fromDriver(Driver driver) {
        DriverDetailsDTO dto = new DriverDetailsDTO();
        dto.id = driver.getId();
        dto.firstName = driver.getFirstName();
        dto.lastName = driver.getLastName();
        dto.email = driver.getEmail();
        dto.phoneNumber = driver.getPhoneNumber();
        dto.status = driver.getStatus() == null ? null : driver.getStatus().name();
        dto.active = driver.getIsActive();
        dto.blocked = driver.getIsBlocked();

        Vehicle vehicle = driver.getVehicle();
        if (vehicle != null) {
            dto.vehicleModel = vehicle.getVehicleModel();
            dto.vehicleType = vehicle.getType() == null ? null : vehicle.getType().name();
            dto.registrationPlate = vehicle.getRegistrationPlate();
        }
        return dto;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getStatus() { return status; }
    public boolean isActive() { return active; }
    public boolean isBlocked() { return blocked; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
}
