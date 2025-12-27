package service.impl;

import dto.user.UserProfileDTO;

import java.util.List;

import domain.entities.*;
import service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public UserProfileDTO getUserProfile(String userId) {

        // Simulated user fetch (ISS purpose)
        User user = new User();
        user.setFirstName("Nenad");
        user.setLastName("Jevremovic");
        user.setUserName("neytan");
        user.setEmail("nenad@example.com");
        user.setPhoneNumber("+38160123456");
        user.setUserType(user.getUserType()); 
        user.setProfilePictureUrl("https://example.com/profile.png");

        // Map Entity -> DTO 
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(userId);
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setGender(user.getGender());
        dto.setUsername(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setUserType(user.getUserType());

        return dto;
    }

	@Override
	public User register(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
