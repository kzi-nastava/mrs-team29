package com.example.driverr_mobile.data.model;

public class AddressResponse {
    private String id;
    private String street;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;
    private String displayName;

    public String getId() { return id; }
    public String getStreet() { return street; }
    public String getStreetNumber() { return streetNumber; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDisplayName() { return displayName; }
}
