package dto.driver;

public class PassengerInfoDTO {

    private String passengerId;
    private String name;
    private String email;

    public PassengerInfoDTO() {}

    public PassengerInfoDTO(String passengerId, String name, String email) {
        this.passengerId = passengerId;
        this.name = name;
        this.email = email;
    }

    public String getPassengerId() { return passengerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
