package service.impl;

import dto.rating.*;
import service.RatingService;

public class RatingServiceImpl implements RatingService {

    @Override
    public RatingResponseDTO submitRating(RatingRequestDTO dto) {

        // Stub:
        // - check date of Ride
        // - if > 3 days -> EXPIRED
        // - else CREATED

        return new RatingResponseDTO(
                "rating-123",
                "CREATED"
        );
    }
}

