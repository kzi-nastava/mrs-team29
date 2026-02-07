package dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import domain.enums.Gender;

public class RegisterRequestDTO {
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotBlank(message = "Password confirmation cannot be blank")
    private String confirmPassword;
    
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private Gender gender;
    
    private String address;
    
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;
    
    private String profilePictureUrl;
    
    public RegisterRequestDTO() {}
    
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
