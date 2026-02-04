package dto.map;

import jakarta.validation.constraints.NotNull;

public class ReverseGeocodeRequestDTO {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    public ReverseGeocodeRequestDTO() {}

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
