package main.java.service;

import main.java.domain.entities.Driver;
import main.java.domain.enums.DriverStatus;

public interface DriverService {
	Driver getAvailableDriver();
	void updateStatus(String driverId, DriverStatus status);
}
