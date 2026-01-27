package repository;

import domain.entities.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {
	Optional<ActivationToken> findByToken(String token);
}

