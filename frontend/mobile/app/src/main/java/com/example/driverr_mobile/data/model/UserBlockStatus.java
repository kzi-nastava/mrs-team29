package com.example.driverr_mobile.data.model;

public class UserBlockStatus {
    private String userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private boolean blocked;
    private String blockNote;
    private String userType;

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isBlocked() { return blocked; }
    public String getBlockNote() { return blockNote; }
    public String getUserType() { return userType; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public void setBlockNote(String blockNote) { this.blockNote = blockNote; }
    public void setUserType(String userType) { this.userType = userType; }
}
