package domain.entities;

import domain.enums.Gender;
import domain.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@NotBlank(message = "First name cannot be blank")
	@Column(name = "first_name")
	private String firstName;
	
	@NotBlank(message = "Last name cannot be blank")
	@Column(name = "last_name")
	private String lastName;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@NotBlank(message = "Username cannot be blank")
	private String userName;
	
	@Column(unique = true, nullable = false)
	@Email(message = "Email must be valid")
	private String email;
	
	@NotBlank(message = "Password cannot be blank")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType type;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@ManyToOne
	@JoinColumn(name = "address_id")
	private Address address;
	
	@Column(name = "profile_picture_url")
	private String profilePictureUrl;
	
	@Column(name = "is_blocked")
	private Boolean isBlocked;
	
	@Column(name = "block_note", length = 500)
	private String blockNote;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "is_activated")
	private Boolean isActivated;
	
	public User() {}
	public User(String firstName, String lastName, Gender gender,
				String userName, String email, String password, UserType type, 
				String phoneNumber, Address address, String profilePictureUrl, boolean isBlocked, boolean isActive) {
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
		this.isActivated = false; // New users need activation
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
	public boolean getIsBlocked() {return Boolean.TRUE.equals(isBlocked);}
	public String getBlockNote() {return blockNote;}
	public boolean getIsActive() { return Boolean.TRUE.equals(isActive);}
	public boolean isActivated() { return Boolean.TRUE.equals(isActivated);}
	
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
	public void setBlockNote(String blockNote) {this.blockNote = blockNote;}
	public void setIsBlocked(boolean isBlocked) {this.isBlocked = isBlocked;}
	public void setActivated(boolean isActivated) {this.isActivated = isActivated;}
}
