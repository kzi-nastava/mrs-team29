package dto.user;

import jakarta.validation.constraints.NotBlank;

public class DriverStatusDTO {
    
    @NotBlank(message = "Active status cannot be blank")
    private Boolean active;
    
    public DriverStatusDTO() {}
    
    public DriverStatusDTO(Boolean active) {
        this.active = active;
    }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
