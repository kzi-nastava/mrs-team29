package service;

import domain.entities.Driver;
import domain.enums.DriverStatus;

public interface DriverService {
	public Driver getAvailableDriver();
	public void updateStatus(String driverId, DriverStatus status);
}
