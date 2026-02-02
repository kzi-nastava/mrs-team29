package service.impl;

import dto.user.*;

import java.util.List;

import org.springframework.stereotype.Service;

import domain.entities.*;
import domain.enums.*;
import service.UserService;
import repository.*;

@Service
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
	            ProfileChangeRequest request = new ProfileChangeRequest(
	                    user,
	                    "PROFILE_UPDATE",
	                    user.toString(),      // on reality JSON
	                    dto.toString()
	            );

	            profileChangeRequestRepository.save(request);
	            return UserProfileDTO.fromUser(user);
	        }

	        user.setFirstName(dto.getFirstName());
	        user.setLastName(dto.getLastName());
	        user.setGender(dto.getGender());
	        user.setPhoneNumber(dto.getPhoneNumber());
	        user.setAddress(dto.getAddress());
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
	    public void changePassword(String userId, PasswordResetDTO dto) {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (!user.getPassword().equals(dto.getOldPassword())) {
	            throw new RuntimeException("Invalid old password");
	        }

	        user.setPassword(dto.getNewPassword());
	        userRepository.save(user);
	    }
	    
	    @Override
	    public List<ProfileChangeRequestDTO> getProfileChangeRequests(String userId) {
	        List<ProfileChangeRequest> requests = profileChangeRequestRepository.findByUserId(userId);
	        return requests.stream()
	            .map(this::mapToDTO)
	            .collect(java.util.stream.Collectors.toList());
	    }
	    
	    @Override
	    public List<ProfileChangeRequestDTO> getAllPendingProfileChangeRequests() {
	        List<ProfileChangeRequest> requests = profileChangeRequestRepository
	            .findByStatus(ChangeRequestStatus.PENDING);
	        return requests.stream()
	            .map(this::mapToDTO)
	            .collect(java.util.stream.Collectors.toList());
	    }
	    
	    @Override
	    public void approveProfileChangeRequest(String requestId) {
	        ProfileChangeRequest request = profileChangeRequestRepository.findById(requestId)
	                .orElseThrow(() -> new RuntimeException("Request not found"));
	        
	        request.setStatus(ChangeRequestStatus.APPROVED);
	        profileChangeRequestRepository.save(request);
	        
	        // Apply the changes to user profile
	        User user = request.getUser();
	        applyProfileChange(user, request.getFieldName(), request.getNewValue());
	        userRepository.save(user);
	    }
	    
	    @Override
	    public void rejectProfileChangeRequest(String requestId) {
	        ProfileChangeRequest request = profileChangeRequestRepository.findById(requestId)
	                .orElseThrow(() -> new RuntimeException("Request not found"));
	        
	        request.setStatus(ChangeRequestStatus.REJECTED);
	        profileChangeRequestRepository.save(request);
	    }
	    
	    private ProfileChangeRequestDTO mapToDTO(ProfileChangeRequest request) {
	        ProfileChangeRequestDTO dto = new ProfileChangeRequestDTO();
	        dto.setId(request.getId());
	        dto.setUserId(request.getUser().getId());
	        dto.setFieldName(request.getFieldName());
	        dto.setOldValue(request.getOldValue());
	        dto.setNewValue(request.getNewValue());
	        dto.setStatus(request.getStatus());
	        dto.setCreatedAt(request.getCreatedAt());
	        return dto;
	    }
	    
	    private void applyProfileChange(User user, String fieldName, String newValue) {
	        switch (fieldName) {
	            case "firstName": user.setFirstName(newValue); break;
	            case "lastName": user.setLastName(newValue); break;
	            case "phoneNumber": user.setPhoneNumber(newValue); break;
	            case "profilePictureUrl": user.setProfilePictureUrl(newValue); break;
	            default: throw new RuntimeException("Unknown field: " + fieldName);
	        }
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
