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

    // getters & setters
}
