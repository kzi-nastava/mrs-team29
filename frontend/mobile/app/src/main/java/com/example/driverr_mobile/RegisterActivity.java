package com.example.driverr_mobile;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.RegisterRequest;
import com.example.driverr_mobile.data.network.ApiClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText firstNameInput = findViewById(R.id.register_first_name);
        TextInputEditText lastNameInput = findViewById(R.id.register_last_name);
        TextInputEditText emailInput = findViewById(R.id.register_email);
        MaterialAutoCompleteTextView genderInput = findViewById(R.id.register_gender);
        TextInputEditText phoneInput = findViewById(R.id.register_phone);
        TextInputEditText addressInput = findViewById(R.id.register_address);
        TextInputEditText passwordInput = findViewById(R.id.register_password);
        TextInputEditText confirmPasswordInput = findViewById(R.id.register_confirm_password);
        MaterialButton registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String firstName = textOf(firstNameInput);
            String lastName = textOf(lastNameInput);
            String email = textOf(emailInput);
            String gender = genderInput.getText() == null ? "" : genderInput.getText().toString().trim();
            String phone = textOf(phoneInput);
            String address = textOf(addressInput);
            String password = textOf(passwordInput);
            String confirmPassword = textOf(confirmPasswordInput);

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            registerButton.setEnabled(false);
            registerButton.setText("Creating...");

            RegisterRequest request = new RegisterRequest(
                    email,
                    password,
                    confirmPassword,
                    firstName,
                    lastName,
                    gender.isEmpty() ? null : gender,
                    address,
                    phone
            );

            ApiClient.getAuthApi().register(request).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<Object>> call, retrofit2.Response<ApiResponse<Object>> response) {
                    registerButton.setEnabled(true);
                    registerButton.setText("Create account");

                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration submitted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) {
                    registerButton.setEnabled(true);
                    registerButton.setText("Create account");
                    Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
