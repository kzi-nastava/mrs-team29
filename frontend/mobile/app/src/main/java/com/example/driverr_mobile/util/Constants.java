package com.example.driverr_mobile.util;

public final class Constants {
    private Constants() {}

    // Mobile app BASE_URL - Use your computer's LAN IP address
    // For mobile testing: Use IP address (e.g., http://192.168.0.12:8081/api/)
    // For web testing: Use localhost (http://localhost:8081/api/)
    // Make sure this matches the BACKEND_URL in backend application.properties
    public static final String BASE_URL = "http://192.168.0.12:8081/api/";
}
