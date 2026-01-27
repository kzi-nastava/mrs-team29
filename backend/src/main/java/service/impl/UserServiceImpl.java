package service.impl;

import dto.user.*;

import java.util.List;
import java.util.UUID;

import domain.entities.*;
import domain.enums.*;
import service.UserService;
import repository.*;

public class UserServiceImpl implements UserService {

		private final UserRepository userRepository;
	    private final ProfileChangeRequestRepository profileChangeRequestRepository;

	    public UserServiceImpl(UserRepository userRepository,
	                           ProfileChangeRequestRepository profileChangeRequestRepository) {
	        this.userRepository = userRepository;
	        this.profileChangeRequestRepository = profileChangeRequestRepository;
	    }
	
   /* @Override
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
    }*/
	
	    @Override
	    public UserProfileDTO getUserProfile(String userId) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        return UserProfileDTO.fromUser(user);
	    }

	    @Override
	    public UserProfileDTO updateProfile(String userId, UpdateUserProfileDTO dto) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (user.getUserType() == UserType.DRIVER) {
	            ProfileChangeRequest request = new ProfileChangeRequest();
	            request.setId(UUID.randomUUID().toString());
	            request.setFieldName("profile");
	            request.setOldValue(user.toString());   // ili JSON
	            request.setNewValue(dto.toString());
	            request.setStatus(ChangeRequestStatus.PENDING);

	            profileChangeRequestRepository.save(request);

	            return UserProfileDTO.fromUser(user);
	        }
	        
	        //
	        user.setFirstName(dto.getFirstName());
	        user.setLastName(dto.getLastName());
	        user.setPhoneNumber(dto.getPhoneNumber());
	        user.setProfilePictureUrl(dto.getProfilePictureUrl());

	        userRepository.save(user);

	        return UserProfileDTO.fromUser(user);
	    }
	    
	    @Override
	    public void changePassword(String userId, ChangePasswordDTO dto) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (!user.getPassword().equals(dto.getOldPassword())) {
	            throw new RuntimeException("Invalid old password");
	        }

	        user.setPassword(dto.getNewPassword());
	        userRepository.save(user);
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
