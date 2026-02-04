package dto.map;

import domain.entities.Address;

public class AddressResponseDTO {

    private String id;
    private String street;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;
    private String displayName;

    public AddressResponseDTO() {}

    public static AddressResponseDTO from(Address address, String displayName) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.id = address.getId();
        dto.street = address.getStreet();
        dto.streetNumber = address.getStreetNumber();
        dto.city = address.getCity();
        dto.postalCode = address.getPostalCode();
        dto.country = address.getCountry();
        dto.latitude = address.getLatitude();
        dto.longitude = address.getLongitude();
        dto.displayName = displayName;
        return dto;
    }

    public String getId() { return id; }
    public String getStreet() { return street; }
    public String getStreetNumber() { return streetNumber; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDisplayName() { return displayName; }

    public void setId(String id) { this.id = id; }
    public void setStreet(String street) { this.street = street; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
