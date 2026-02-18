package com.driverr.Driverr.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

//@Disabled("Requires running frontend/backend and local ChromeDriver")
class FavoriteRoutesE2ETest {

    private WebDriver driver;
    private RestTemplate restTemplate;
    private static final String API_BASE_URL = "http://localhost:8080/api";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        restTemplate = new RestTemplate();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void orderRideFromFavorite_routeCardVisible_ordersRide() {
        String baseUrl = "http://localhost:4200";
        String testUserId = "TODO-USER-ID";

        driver.get(baseUrl + "/main-page");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.setItem('userId', arguments[0]);", testUserId);

        driver.get(baseUrl + "/order-ride");

        // Ensure favorite route exists for testUserId before running the test
        ensureFavoriteRouteExists(testUserId);
        
        driver.findElement(By.cssSelector("[data-testid='ride-order-favorites-section']"));
        driver.findElement(By.cssSelector("[data-testid='ride-order-favorite-order']")).click();

        driver.findElement(By.cssSelector("[data-testid='ride-order-success']"));
    }
    
    /**
     * Creates a favorite route for the test user if one doesn't already exist.
     * Creates pickup and destination addresses, then creates the favorite route.
     */
    private void ensureFavoriteRouteExists(String userId) {
        try {
            // Create pickup address
            String pickupAddressId = createTestAddress(
                "Bulevar oslobođenja", "46", "Novi Sad", "21000", "Serbia", 45.2671, 19.8335
            );
            
            // Create destination address
            String destinationAddressId = createTestAddress(
                "Trg slobode", "1", "Novi Sad", "21000", "Serbia", 45.2550, 19.8450
            );
            
            // Create favorite route
            createFavoriteRoute(userId, "Test Route", pickupAddressId, destinationAddressId);
            
        } catch (Exception e) {
            System.out.println("Warning: Could not create favorite route: " + e.getMessage());
            // Route might already exist, continue with test
        }
    }
    
    /**
     * Creates a test address via API and returns its ID.
     */
    @SuppressWarnings("unchecked")
    private String createTestAddress(String street, String streetNumber, String city, 
                                     String postalCode, String country, double lat, double lon) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("street", street);
        addressData.put("streetNumber", streetNumber);
        addressData.put("city", city);
        addressData.put("postalCode", postalCode);
        addressData.put("country", country);
        addressData.put("latitude", lat);
        addressData.put("longitude", lon);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(addressData, headers);
        Map<String, Object> response = restTemplate.postForObject(
            API_BASE_URL + "/addresses/save", 
            request, 
            Map.class
        );
        
        return (String) response.get("id");
    }
    
    /**
     * Creates a favorite route via API.
     */
    private void createFavoriteRoute(String userId, String name, String pickupId, String destId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> routeData = new HashMap<>();
        routeData.put("userId", userId);
        routeData.put("name", name);
        routeData.put("pickupAddressId", pickupId);
        routeData.put("destinationAddressId", destId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(routeData, headers);
        restTemplate.postForObject(
            API_BASE_URL + "/favorite-routes", 
            request, 
            Map.class
        );
    }

}
