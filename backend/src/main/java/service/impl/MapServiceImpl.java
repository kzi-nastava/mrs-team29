package service.impl;

import dto.map.GeocodeResultDTO;
import dto.map.RouteResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import service.MapService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class MapServiceImpl implements MapService {

    private final RestTemplate restTemplate;

    @Value("${map.nominatim.base-url:https://nominatim.openstreetmap.org}")
    private String nominatimBaseUrl;

    @Value("${map.osrm.base-url:http://router.project-osrm.org}")
    private String osrmBaseUrl;

    @Value("${map.user-agent:Driverr/1.0 (contact@example.com)}")
    private String userAgent;

    @Value("${map.nominatim.email:contact@example.com}")
    private String nominatimEmail;

    @Value("${map.referer:http://localhost:4200}")
    private String referer;

    public MapServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GeocodeResultDTO geocode(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(nominatimBaseUrl)
                .path("/search")
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .queryParam("addressdetails", 1)
            .queryParam("email", nominatimEmail)
                .queryParam("q", query)
            .build()
            .encode()
                .toUri();

        List<Map<String, Object>> results = exchangeForList(uri);
        if (results.isEmpty()) {
            throw new RuntimeException("No location found for query");
        }

        return mapNominatimResult(results.get(0));
    }

    @Override
    public GeocodeResultDTO reverseGeocode(double latitude, double longitude) {
        URI uri = UriComponentsBuilder.fromHttpUrl(nominatimBaseUrl)
                .path("/reverse")
                .queryParam("format", "json")
                .queryParam("addressdetails", 1)
            .queryParam("email", nominatimEmail)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
            .build()
            .encode()
                .toUri();

        Map<String, Object> result = exchangeForMap(uri);
        return mapNominatimResult(result);
    }

    @Override
    public RouteResultDTO getRoute(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        String coordinates = String.format("%s,%s;%s,%s", fromLongitude, fromLatitude, toLongitude, toLatitude);
        URI uri = UriComponentsBuilder.fromHttpUrl(osrmBaseUrl)
                .path("/route/v1/driving/")
                .path(coordinates)
                .queryParam("overview", "false")
                .queryParam("steps", "false")
                .build(true)
                .toUri();

        Map<String, Object> response = exchangeForMap(uri);
        Object routesObj = response.get("routes");
        if (!(routesObj instanceof List<?> routes) || routes.isEmpty()) {
            throw new RuntimeException("No route available");
        }

        Map<String, Object> route = (Map<String, Object>) routes.get(0);
        double distance = parseDouble(route.get("distance"));
        int durationSeconds = (int) Math.round(parseDouble(route.get("duration")));

        return new RouteResultDTO(distance, durationSeconds);
    }

    private List<Map<String, Object>> exchangeForList(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, userAgent);
        headers.set(HttpHeaders.REFERER, referer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody() == null ? List.of() : response.getBody();
    }

    private Map<String, Object> exchangeForMap(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, userAgent);
        headers.set(HttpHeaders.REFERER, referer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null) {
            throw new RuntimeException("No response from map provider");
        }

        return response.getBody();
    }

    private GeocodeResultDTO mapNominatimResult(Map<String, Object> result) {
        GeocodeResultDTO dto = new GeocodeResultDTO();
        dto.setDisplayName(stringValue(result.get("display_name")));
        dto.setLatitude(parseDouble(result.get("lat")));
        dto.setLongitude(parseDouble(result.get("lon")));

        Map<String, Object> address = (Map<String, Object>) result.get("address");
        String street = pick(address, "road", "pedestrian", "footway", "path");
        String number = pick(address, "house_number");
        String city = pick(address, "city", "town", "village", "municipality", "county");
        String postal = pick(address, "postcode");
        String country = pick(address, "country");

        dto.setStreet(defaultIfBlank(street, "Unknown"));
        dto.setStreetNumber(defaultIfBlank(number, "0"));
        dto.setCity(defaultIfBlank(city, "Unknown"));
        dto.setPostalCode(postal);
        dto.setCountry(defaultIfBlank(country, "Unknown"));

        return dto;
    }

    private String pick(Map<String, Object> map, String... keys) {
        if (map == null) {
            return null;
        }
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }
        return null;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private double parseDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            return 0;
        }
        return Double.parseDouble(value.toString());
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
