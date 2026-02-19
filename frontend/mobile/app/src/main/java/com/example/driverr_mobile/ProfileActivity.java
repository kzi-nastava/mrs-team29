package com.example.driverr_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ArrayAdapter;

import com.example.driverr_mobile.data.model.ChangePasswordRequest;
import com.example.driverr_mobile.data.model.UpdateUserProfileRequest;
import com.example.driverr_mobile.data.model.UserProfile;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView avatarText;
    private TextView workingHoursValue;
    private View workingHoursSection;
    private View blockStatusSection;
    private TextView blockNoteText;
    private View passwordSection;

    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private MaterialAutoCompleteTextView genderInput;
    private TextInputEditText emailInput;
    private TextInputEditText usernameInput;
    private TextInputEditText phoneInput;

    private TextInputEditText oldPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;

    private MaterialButton saveButton;
    private MaterialButton resetButton;
    private MaterialButton togglePasswordButton;
    private MaterialButton updatePasswordButton;

    private String userId;
    private boolean isDriver;

    private UserProfile profileDefaults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        avatarText = findViewById(R.id.profile_avatar_text);
        workingHoursValue = findViewById(R.id.profile_working_hours_value);
        workingHoursSection = findViewById(R.id.profile_working_hours_section);
        blockStatusSection = findViewById(R.id.profile_block_status_section);
        blockNoteText = findViewById(R.id.profile_block_note);
        passwordSection = findViewById(R.id.profile_password_section);

        firstNameInput = findViewById(R.id.profile_first_name);
        lastNameInput = findViewById(R.id.profile_last_name);
        genderInput = findViewById(R.id.profile_gender);
        emailInput = findViewById(R.id.profile_email);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);

        // Set up gender dropdown
        String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderInput.setAdapter(genderAdapter);

        oldPasswordInput = findViewById(R.id.profile_old_password);
        newPasswordInput = findViewById(R.id.profile_new_password);
        confirmPasswordInput = findViewById(R.id.profile_confirm_password);

        saveButton = findViewById(R.id.profile_save_button);
        resetButton = findViewById(R.id.profile_reset_button);
        togglePasswordButton = findViewById(R.id.profile_toggle_password_button);
        updatePasswordButton = findViewById(R.id.profile_update_password_button);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null || userId.isBlank()) {
            Toast.makeText(this, "User not loaded. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> saveProfile());
        resetButton.setOnClickListener(v -> resetProfile());
        togglePasswordButton.setOnClickListener(v -> togglePasswordSection());
        updatePasswordButton.setOnClickListener(v -> changePassword());

        passwordSection.setVisibility(View.GONE);
        workingHoursSection.setVisibility(View.GONE);
        blockStatusSection.setVisibility(View.GONE);

        loadProfile();
    }

    private void loadProfile() {
        ApiClient.getUserApi().getProfile(userId).enqueue(new retrofit2.Callback<UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserProfile profile = response.body();
                profileDefaults = profile;

                firstNameInput.setText(nullToEmpty(profile.getFirstName()));
                lastNameInput.setText(nullToEmpty(profile.getLastName()));
                genderInput.setText(nullToEmpty(profile.getGender()), false);
                emailInput.setText(nullToEmpty(profile.getEmail()));
                usernameInput.setText(nullToEmpty(profile.getUsername()));
                phoneInput.setText(nullToEmpty(profile.getPhoneNumber()));

                String firstLetter = profile.getFirstName() != null && !profile.getFirstName().isBlank()
                        ? profile.getFirstName().substring(0, 1).toUpperCase()
                        : "U";
                avatarText.setText(firstLetter);

                isDriver = "DRIVER".equalsIgnoreCase(profile.getUserType());
                if (isDriver) {
                    loadWorkingHours();
                
                // Show block status if blocked
                if (profile.isBlocked()) {
                    blockStatusSection.setVisibility(View.VISIBLE);
                    String note = profile.getBlockNote();
                    blockNoteText.setText(note != null && !note.isBlank() 
                        ? note 
                        : "Your account has been blocked. Please contact support.");
                }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWorkingHours() {
        ApiClient.getDriverApi().getWorkingHours(userId).enqueue(new retrofit2.Callback<Double>() {
            @Override
            public void onResponse(retrofit2.Call<Double> call, retrofit2.Response<Double> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                double hours = response.body();
                workingHoursValue.setText(String.format("%.1f", hours));
                workingHoursSection.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(retrofit2.Call<Double> call, Throwable t) {
                // ignore
            }
        });
    }

    private void saveProfile() {
        String firstName = textOf(firstNameInput);
        String lastName = textOf(lastNameInput);
        String gender = textOf(genderInput);
        String username = textOf(usernameInput);
        String phone = textOf(phoneInput);
        Object address = profileDefaults == null ? null : profileDefaults.getAddress();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "First name and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Saving...");

        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                firstName,
                lastName,
                gender.isEmpty() ? null : gender,
                username.isEmpty() ? null : username,
                phone.isEmpty() ? null : phone,
            address,
                null
        );

        ApiClient.getUserApi().updateProfile(userId, request)
                .enqueue(new retrofit2.Callback<UserProfile>() {
                    @Override
                    public void onResponse(retrofit2.Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save changes");

                        if (response.isSuccessful()) {
                            String message = isDriver
                                    ? "Profile update request submitted"
                                    : "Profile updated successfully";
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (response.body() != null) {
                                profileDefaults = response.body();
                            String updatedAvatar = profileDefaults.getFirstName() != null
                                && !profileDefaults.getFirstName().isBlank()
                                ? profileDefaults.getFirstName().substring(0, 1).toUpperCase()
                                : "U";
                            avatarText.setText(updatedAvatar);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, extractErrorMessage(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<UserProfile> call, Throwable t) {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save changes");
                        Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetProfile() {
        if (profileDefaults == null) {
            return;
        }

        firstNameInput.setText(nullToEmpty(profileDefaults.getFirstName()));
        lastNameInput.setText(nullToEmpty(profileDefaults.getLastName()));
        genderInput.setText(nullToEmpty(profileDefaults.getGender()), false);
        emailInput.setText(nullToEmpty(profileDefaults.getEmail()));
        usernameInput.setText(nullToEmpty(profileDefaults.getUsername()));
        phoneInput.setText(nullToEmpty(profileDefaults.getPhoneNumber()));
    }

    private void togglePasswordSection() {
        if (passwordSection.getVisibility() == View.VISIBLE) {
            passwordSection.setVisibility(View.GONE);
            oldPasswordInput.setText("");
            newPasswordInput.setText("");
            confirmPasswordInput.setText("");
        } else {
            passwordSection.setVisibility(View.VISIBLE);
        }
    }

    private void changePassword() {
        String oldPassword = textOf(oldPasswordInput);
        String newPassword = textOf(newPasswordInput);
        String confirmPassword = textOf(confirmPasswordInput);

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all password fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        updatePasswordButton.setEnabled(false);
        updatePasswordButton.setText("Updating...");

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);
        ApiClient.getUserApi().changePassword(userId, request)
                .enqueue(new retrofit2.Callback<Object>() {
                    @Override
                    public void onResponse(retrofit2.Call<Object> call, retrofit2.Response<Object> response) {
                        updatePasswordButton.setEnabled(true);
                        updatePasswordButton.setText("Update password");

                        if (response.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            passwordSection.setVisibility(View.GONE);
                            oldPasswordInput.setText("");
                            newPasswordInput.setText("");
                            confirmPasswordInput.setText("");
                        } else {
                            Toast.makeText(ProfileActivity.this, extractErrorMessage(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                        updatePasswordButton.setEnabled(true);
                        updatePasswordButton.setText("Update password");
                        Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String textOf(MaterialAutoCompleteTextView input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        if (response.errorBody() == null) {
            return "Request failed";
        }
        try {
            String body = response.errorBody().string();
            String message = new Gson().fromJson(body, com.example.driverr_mobile.data.model.ApiResponse.class)
                    .getMessage();
            if (message != null && !message.isBlank()) {
                return message;
            }
            return body;
        } catch (Exception e) {
            return "Request failed";
        }
    }
}
