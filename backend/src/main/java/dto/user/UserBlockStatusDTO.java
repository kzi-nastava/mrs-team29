package dto.user;

public record UserBlockStatusDTO(
    String userId,
    String userName,
    String email,
    String firstName,
    String lastName,
    boolean blocked,
    String blockNote,
    String userType
) {}
