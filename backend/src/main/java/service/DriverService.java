package service;

import java.time.LocalDate;
import java.util.List;

import domain.entities.Driver;
import domain.enums.DriverStatus;
import dto.driver.*;

public interface DriverService {
	public Driver getAvailableDriver();
	public void updateStatus(String driverId, DriverStatus status);
	public Driver registerDriver(DriverRegistrationDTO dto);
	public List<ActiveDriverDTO> getActiveDrivers();
	List<DriverRideHistoryDTO> getDriverRideHistory(String driverId, LocalDate from, LocalDate to);
	void activateDriver(String token, String newPassword);
	double getWorkingHoursLast24h(String driverId);
}
