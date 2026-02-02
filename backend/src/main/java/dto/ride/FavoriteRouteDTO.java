package dto.ride;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class FavoriteRouteDTO {
    
    private String id;
    
    @NotBlank(message = "Route name is required")
    private String name;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Pickup address ID is required")
    private String pickupAddressId;
    
    @NotBlank(message = "Destination address ID is required")
    private String destinationAddressId;
    
    private List<String> stopAddressIds;
    
    public FavoriteRouteDTO() {}
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUserId() { return userId; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
}
