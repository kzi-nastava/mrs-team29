package service;

import dto.rating.*;

public interface RatingService {

    RatingResponseDTO submitRating(RatingRequestDTO dto);
}
