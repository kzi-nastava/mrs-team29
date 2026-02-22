package repository;

import domain.entities.DriverInconsistencyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverInconsistencyNoteRepository extends JpaRepository<DriverInconsistencyNote, String> {
    
    List<DriverInconsistencyNote> findByRideId(String rideId);
    
    List<DriverInconsistencyNote> findByPassengerId(String passengerId);
}
