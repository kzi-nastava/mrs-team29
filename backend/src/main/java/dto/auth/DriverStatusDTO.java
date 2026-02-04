package dto.auth;

import jakarta.validation.constraints.NotNull;

public class DriverStatusDTO {
    
    @NotNull(message = "Active status is required")
    private Boolean active;
    
    public DriverStatusDTO() {}
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
