package repository;

import domain.entities.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {
}

