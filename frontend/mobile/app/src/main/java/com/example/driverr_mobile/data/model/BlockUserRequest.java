package com.example.driverr_mobile.data.model;

public class BlockUserRequest {
    private String userId;
    private boolean blocked;
    private String blockNote;

    public BlockUserRequest(String userId, boolean blocked, String blockNote) {
        this.userId = userId;
        this.blocked = blocked;
        this.blockNote = blockNote;
    }

    public String getUserId() { return userId; }
    public boolean isBlocked() { return blocked; }
    public String getBlockNote() { return blockNote; }
}
