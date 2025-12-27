package service;

import domain.entities.Driver;
import domain.enums.DriverStatus;
import dto.driver.*;

public interface DriverService {
	public Driver getAvailableDriver();
	public void updateStatus(String driverId, DriverStatus status);
	Driver registerDriver(DriverRegistrationDTO dto);
}
