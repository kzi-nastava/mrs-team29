package controller;

import dto.rating.*;
import jakarta.validation.Valid;
import service.RatingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:4200")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponseDTO> submitRating(
            @Valid @RequestBody RatingRequestDTO dto) {

        RatingResponseDTO response = ratingService.submitRating(dto);
        return ResponseEntity.ok(response);
    }
}
