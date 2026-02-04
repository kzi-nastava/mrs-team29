package repository;

import domain.entities.Address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String>{
    List<Address> findByIdIn(List<String> ids);
    List<Address> findByCityIgnoreCase(String city);
    List<Address> findByStreetIgnoreCase(String street);
    java.util.Optional<Address> findByLatitudeAndLongitude(double latitude, double longitude);
}
