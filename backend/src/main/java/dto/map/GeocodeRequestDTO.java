package dto.map;

import jakarta.validation.constraints.NotBlank;

public class GeocodeRequestDTO {

    @NotBlank(message = "Query is required")
    private String query;

    public GeocodeRequestDTO() {}

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
