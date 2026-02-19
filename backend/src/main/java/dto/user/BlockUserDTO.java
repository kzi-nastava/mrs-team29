package dto.user;

public record BlockUserDTO(
    String userId,
    boolean blocked,
    String blockNote
) {}
