package main.java.domain.entities;

import main.java.domain.enums.Gender;
import main.java.domain.enums.UserType;

public class User {
	private String id;
	private String firstName;
	private String lastName;
	private Gender gender;
	private String userName;
	private String email;
	private String password;
	private UserType type;
	private String phoneNumber;
	private Address address;
	private String profilePictureUrl;
	private boolean isBlocked;
	private boolean isActive;
	
	public User() {}
	public User(String id, String firstName, String lastName, Gender gender,
				String userName, String email, String password, UserType type, 
				String phoneNumber, Address address, String profilePictureUrl, boolean isBlocked, boolean isActive) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.type = type;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.profilePictureUrl = profilePictureUrl;
		this.isActive = isActive;
		this.isBlocked = isBlocked;
	}
	
	public String getId() {return id;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public Gender getGender() {return gender;}
	public String getUserName() {return userName;}
	public String getEmail() {return email;}
	public String getPassword() {return password;}
	public UserType getUserType() {return type;}
	public String getPhoneNumber() {return phoneNumber;}
	public Address getAddress() {return address;}
	public String getProfilePictureUrl() {return profilePictureUrl;}
	public boolean getIsBlocked() {return isBlocked;}
	public boolean getIsActive() { return isActive;}
	
	public void setId(String id) {this.id = id;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setGender(Gender gender) {this.gender = gender;}
	public void setUserName(String userName) {this.userName = userName;}
	public void setEmail(String email) {this.email = email;}
	public void setPassword(String password) {this.password = password;}
	public void setUserType(UserType type) {this.type = type;}
	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
	public void setAddress(Address address) {this.address = address;}
	public void setProfilePictureUrl(String profilePictureUrl) {this.profilePictureUrl = profilePictureUrl;}
	public void setIsActive(boolean isActive) {this.isActive = isActive;}
	public void setIsBlocked(boolean isBlocked) {this.isBlocked = isBlocked;}
}
