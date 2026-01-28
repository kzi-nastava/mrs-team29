package repository;

import domain.entities.Address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String>{
	@Override
    Optional<Address> findById(String id);

    @Override
    List<Address> findAll();
    List<Address> findByIdIn(List<String> ids);
    boolean existsById(String id);

    List<Address> findByCityIgnoreCase(String city);
    List<Address> findByStreetIgnoreCase(String street);
}
