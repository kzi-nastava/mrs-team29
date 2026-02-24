package service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import domain.entities.Driver;
import domain.entities.Rating;
import domain.entities.Ride;
import domain.entities.User;
import domain.entities.Vehicle;
import domain.enums.RideStatus;
import dto.rating.RatingRequestDTO;
import dto.rating.RatingResponseDTO;
import repository.RatingRepository;
import repository.RideRepository;
import repository.UserRepository;
import service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RatingServiceImpl(
            RatingRepository ratingRepository,
            RideRepository rideRepository,
            UserRepository userRepository
    ) {
        this.ratingRepository = ratingRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RatingResponseDTO submitRating(RatingRequestDTO dto) {
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User passenger = userRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        if (ride.getPassengers() == null || ride.getPassengers().stream().noneMatch(p -> p.getId().equals(passenger.getId()))) {
            throw new RuntimeException("Passenger is not part of this ride");
        }

        if (ride.getStatus() != RideStatus.FINISHED) {
            throw new RuntimeException("Ride must be finished before rating");
        }

        LocalDateTime rideFinishedAt = getRideFinishedAt(ride);
        if (rideFinishedAt == null) {
            throw new RuntimeException("Ride finished time not found");
        }

        if (ratingRepository.existsByRide_IdAndPassenger_Id(ride.getId(), passenger.getId())) {
            Rating existing = ratingRepository.findByRide_IdAndPassenger_Id(ride.getId(), passenger.getId()).orElse(null);
            return new RatingResponseDTO(existing == null ? null : existing.getId(), "ALREADY_RATED");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(rideFinishedAt.plusDays(3))) {
            return new RatingResponseDTO(null, "EXPIRED");
        }

        Driver driver = ride.getDriver();
        if (driver == null) {
            throw new RuntimeException("Driver not assigned to ride");
        }

        Vehicle vehicle = driver.getVehicle();
        if (vehicle == null) {
            throw new RuntimeException("Driver vehicle not found");
        }

        Rating rating = new Rating(
                ride,
                passenger,
                driver,
                vehicle,
                dto.getDriverRating(),
                dto.getVehicleRating(),
                dto.getComment(),
                rideFinishedAt,
                now,
                false
        );

        rating = ratingRepository.save(rating);
        return new RatingResponseDTO(rating.getId(), "CREATED");
    }

    private LocalDateTime getRideFinishedAt(Ride ride) {
        if (ride.getTimestamps() == null || ride.getTimestamps().isEmpty()) {
            return null;
        }
        return ride.getTimestamps().get(ride.getTimestamps().size() - 1);
    }
}

