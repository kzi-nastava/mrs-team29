package main.java.service;

import java.util.List;

import main.java.domain.entities.User;

public interface UserService {
	public User register(User user);
	public User getByUsername(String username);
	public List<User> getAll();
}
