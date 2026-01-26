package domain.entities;

import java.time.LocalDateTime;

public class ActivationToken {
    private String id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
    
    public ActivationToken() {}
    
    public ActivationToken(String id, String userId, String token, LocalDateTime expiresAt, boolean used) {
    	this.id = id;
    	this.userId = userId;
    	this.token = token;
    	this.expiresAt = expiresAt;
    	this.used = used;
    }
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public LocalDateTime getExpiresAt() {return expiresAt; }
    public boolean isUsed() { return used; }
    
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setToken(String token) { this.token = token; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setUsed (boolean used) {this.used = used; }
    
}
