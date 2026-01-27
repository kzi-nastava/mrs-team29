package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.*;
import domain.enums.*;

@Repository
public interface ProfileChangeRequestRepository
        extends JpaRepository<ProfileChangeRequest, String> {

    List<ProfileChangeRequest> findByStatus(ChangeRequestStatus status);

    List<ProfileChangeRequest> findByUserId(String userId);
}

