package service.impl;

import dto.driver.DriverRegistrationDTO;
import domain.entities.*;
import domain.enums.*;
import service.DriverService;

public class DriverServiceImpl implements DriverService {

    @Override
    public Driver registerDriver(DriverRegistrationDTO dto) {

    	Driver driver = new Driver();

        // === USER FIELDS (inherited) ===
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setGender(dto.getGender());
        driver.setUserName(dto.getUsername());
        driver.setEmail(dto.getEmail());
        driver.setPassword(dto.getPassword());
        driver.setUserType(dto.getUserType());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setAddress(dto.getAddress());
        driver.setProfilePictureUrl(dto.getProfilePictureUrl());
        

        // === DRIVER FIELDS ===
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel(dto.getVehicleModel());
        vehicle.setType(dto.getVehicleType());
        vehicle.setRegistrationPlate(dto.getRegistrationPlate());
        vehicle.setSeats(dto.getSeats());
        vehicle.setBabiesAllowed(dto.isAllowsPets());
        vehicle.setPetsAllowed(dto.isAllowsBabies());

        driver.setVehicle(vehicle);
        driver.setStatus(DriverStatus.AVAILABLE);

        return driver;
    }

	@Override
	public Driver getAvailableDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStatus(String driverId, DriverStatus status) {
		// TODO Auto-generated method stub
		
	}
}
