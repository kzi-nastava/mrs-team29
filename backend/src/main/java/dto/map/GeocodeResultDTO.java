package dto.map;

public class GeocodeResultDTO {

    private String displayName;
    private String street;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

    public GeocodeResultDTO() {}

    public String getDisplayName() { return displayName; }
    public String getStreet() { return street; }
    public String getStreetNumber() { return streetNumber; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setStreet(String street) { this.street = street; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
