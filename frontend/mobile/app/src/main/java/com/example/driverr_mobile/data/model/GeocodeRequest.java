package com.example.driverr_mobile.data.model;

public class GeocodeRequest {
    private String query;

    public GeocodeRequest() {}

    public GeocodeRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
}
