package dto.map;

import jakarta.validation.constraints.NotNull;

public class AddressSaveRequestDTO {

    private String displayName;
    private String street;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    public AddressSaveRequestDTO() {}

    public String getDisplayName() { return displayName; }
    public String getStreet() { return street; }
    public String getStreetNumber() { return streetNumber; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setStreet(String street) { this.street = street; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
