package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domain.entities.Address;

public final class AddressParser {
    private static final Pattern ADDRESS_PATTERN =
            Pattern.compile("^(.+?)\\s+(\\d+[A-Za-z]?(-[A-Za-z])?)$");
    private static final String DEFAULT_CITY = "Unknown";
    private static final String DEFAULT_COUNTRY = "Unknown";

    private AddressParser() {}

    public static Address parseAddressLine(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }

        String trimmed = rawAddress.trim().replaceAll("\\s+", " ");
        Matcher matcher = ADDRESS_PATTERN.matcher(trimmed);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Address must be in format 'Street name Number'");
        }

        String street = matcher.group(1).trim();
        String streetNumber = matcher.group(2).trim();

        Address address = new Address();
        address.setStreet(street);
        address.setStreetNumber(streetNumber);
        address.setCity(DEFAULT_CITY);
        address.setCountry(DEFAULT_COUNTRY);
        address.setLatitude(0.0);
        address.setLongitude(0.0);
        return address;
    }
}
