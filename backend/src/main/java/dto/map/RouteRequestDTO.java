package dto.map;

import jakarta.validation.constraints.NotNull;

public class RouteRequestDTO {

    @NotNull(message = "From latitude is required")
    private Double fromLatitude;

    @NotNull(message = "From longitude is required")
    private Double fromLongitude;

    @NotNull(message = "To latitude is required")
    private Double toLatitude;

    @NotNull(message = "To longitude is required")
    private Double toLongitude;

    public RouteRequestDTO() {}

    public Double getFromLatitude() { return fromLatitude; }
    public Double getFromLongitude() { return fromLongitude; }
    public Double getToLatitude() { return toLatitude; }
    public Double getToLongitude() { return toLongitude; }

    public void setFromLatitude(Double fromLatitude) { this.fromLatitude = fromLatitude; }
    public void setFromLongitude(Double fromLongitude) { this.fromLongitude = fromLongitude; }
    public void setToLatitude(Double toLatitude) { this.toLatitude = toLatitude; }
    public void setToLongitude(Double toLongitude) { this.toLongitude = toLongitude; }
}
