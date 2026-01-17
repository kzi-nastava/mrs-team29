package controller;

import dto.rating.*;
import service.RatingService;
import service.impl.RatingServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService = new RatingServiceImpl();

    @PostMapping
    public ResponseEntity<RatingResponseDTO> submitRating(
            @RequestBody RatingRequestDTO dto) {

        RatingResponseDTO response = ratingService.submitRating(dto);
        return ResponseEntity.ok(response);
    }
}
