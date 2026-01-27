package domain.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import domain.entities.*;

@Entity
@Table(name = "activation_token")
public class ActivationToken {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
	
	@OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@Column(nullable = false, unique = true)
    private String token;
	
	@Column(nullable = false)
    private LocalDateTime expiresAt;
	
    private boolean used;
    
    public ActivationToken() {}
    
    public ActivationToken(String id, User user, String token, LocalDateTime expiresAt, boolean used) {
    	this.id = id;
    	this.user = user;
    	this.token = token;
    	this.expiresAt = expiresAt;
    	this.used = used;
    }
    
    public String getId() { return id; }
    public User getUserId() { return user; }
    public String getToken() { return token; }
    public LocalDateTime getExpiresAt() {return expiresAt; }
    public boolean isUsed() { return used; }
    
    public void setId(String id) { this.id = id; }
    public void setUserId(User user) { this.user = user; }
    public void setToken(String token) { this.token = token; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setUsed (boolean used) {this.used = used; }
    
}
