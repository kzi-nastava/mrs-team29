package dto.rating;

public class RatingResponseDTO {

    private String ratingId;
    private String status; // CREATED / EXPIRED

    public RatingResponseDTO() {}

    public RatingResponseDTO(String ratingId, String status) {
        this.ratingId = ratingId;
        this.status = status;
    }

    public String getRatingId() { return ratingId; }
    public String getStatus() { return status; }

    public void setRatingId(String ratingId) { this.ratingId = ratingId; }
    public void setStatus(String status) { this.status = status; }
}
