package service;

import java.util.List;

import domain.entities.User;
import dto.user.*;

public interface UserService {
	public User register(User user);
	public User getByUsername(String username);
	public List<User> getAll();
	public UserProfileDTO getUserProfile(String userId);
}
