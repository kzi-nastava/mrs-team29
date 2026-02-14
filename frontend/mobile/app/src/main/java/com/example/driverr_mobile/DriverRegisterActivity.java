package com.example.driverr_mobile;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.DriverRegisterRequest;
import com.example.driverr_mobile.data.network.ApiClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.driver_register_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText firstNameInput = findViewById(R.id.driver_register_first_name);
        TextInputEditText lastNameInput = findViewById(R.id.driver_register_last_name);
        MaterialAutoCompleteTextView genderInput = findViewById(R.id.driver_register_gender);
        TextInputEditText usernameInput = findViewById(R.id.driver_register_username);
        TextInputEditText emailInput = findViewById(R.id.driver_register_email);
        TextInputEditText passwordInput = findViewById(R.id.driver_register_password);
        TextInputEditText phoneInput = findViewById(R.id.driver_register_phone);
        TextInputEditText vehicleModelInput = findViewById(R.id.driver_register_vehicle_model);
        MaterialAutoCompleteTextView vehicleTypeInput = findViewById(R.id.driver_register_vehicle_type);
        TextInputEditText registrationPlateInput = findViewById(R.id.driver_register_registration_plate);
        TextInputEditText seatsInput = findViewById(R.id.driver_register_seats);
        MaterialCheckBox allowsPetsInput = findViewById(R.id.driver_register_allows_pets);
        MaterialCheckBox allowsBabiesInput = findViewById(R.id.driver_register_allows_babies);
        MaterialButton registerButton = findViewById(R.id.driver_register_button);

        registerButton.setOnClickListener(v -> {
            String firstName = textOf(firstNameInput);
            String lastName = textOf(lastNameInput);
            String gender = textOf(genderInput);
            String username = textOf(usernameInput);
            String email = textOf(emailInput);
            String password = textOf(passwordInput);
            String phone = textOf(phoneInput);
            String vehicleModel = textOf(vehicleModelInput);
            String vehicleType = textOf(vehicleTypeInput);
            String registrationPlate = textOf(registrationPlateInput);
            String seatsText = textOf(seatsInput);

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()
                    || password.isEmpty() || vehicleModel.isEmpty() || vehicleType.isEmpty()
                    || registrationPlate.isEmpty() || seatsText.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int seats;
            try {
                seats = Integer.parseInt(seatsText);
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Seats must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (seats < 1) {
                Toast.makeText(this, "Seats must be at least 1", Toast.LENGTH_SHORT).show();
                return;
            }

            registerButton.setEnabled(false);
            registerButton.setText("Registering...");

            DriverRegisterRequest request = new DriverRegisterRequest(
                    firstName,
                    lastName,
                    gender.isEmpty() ? null : gender,
                    username,
                    email,
                    password,
                    phone.isEmpty() ? null : phone,
                    vehicleModel,
                    vehicleType,
                    registrationPlate,
                    seats,
                    allowsPetsInput.isChecked(),
                    allowsBabiesInput.isChecked()
            );

            ApiClient.getDriverApi().registerDriver(request)
                    .enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ApiResponse<Object>> call,
                                               retrofit2.Response<ApiResponse<Object>> response) {
                            registerButton.setEnabled(true);
                            registerButton.setText("Register driver");

                            if (response.isSuccessful()) {
                                String message = response.body() != null && response.body().getMessage() != null
                                        ? response.body().getMessage()
                                        : "Driver registered successfully";
                                Toast.makeText(DriverRegisterActivity.this, message, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                String errorMessage = extractErrorMessage(response);
                                Toast.makeText(DriverRegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) {
                            registerButton.setEnabled(true);
                            registerButton.setText("Register driver");
                            Toast.makeText(DriverRegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        findViewById(R.id.driver_register_footer).setOnClickListener(v -> finish());
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String textOf(MaterialAutoCompleteTextView input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        if (response.errorBody() == null) {
            return "Driver registration failed";
        }
        try {
            String body = response.errorBody().string();
            ApiResponse<?> apiResponse = new Gson().fromJson(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getMessage() != null && !apiResponse.getMessage().isBlank()) {
                return apiResponse.getMessage();
            }
        } catch (Exception ignored) {
        }
        return "Driver registration failed";
    }
}
