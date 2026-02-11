package com.example.driverr_mobile;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.LoginRequest;
import com.example.driverr_mobile.data.model.LoginResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText emailInput = findViewById(R.id.login_email);
        TextInputEditText passwordInput = findViewById(R.id.login_password);
        MaterialButton loginButton = findViewById(R.id.login_button);
        SessionManager sessionManager = new SessionManager(this);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText() == null ? "" : emailInput.getText().toString().trim();
            String password = passwordInput.getText() == null ? "" : passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            LoginRequest request = new LoginRequest(email, password);
            ApiClient.getAuthApi().login(request).enqueue(new retrofit2.Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<LoginResponse>> call, retrofit2.Response<ApiResponse<LoginResponse>> response) {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");

                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        sessionManager.saveAuth(response.body().getData());
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = extractErrorMessage(response);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<LoginResponse>> call, Throwable t) {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                    Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.login_footer).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        if (response.errorBody() == null) {
            return "Login failed";
        }
        try {
            String body = response.errorBody().string();
            ApiResponse<?> apiResponse = new Gson().fromJson(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getMessage() != null && !apiResponse.getMessage().isBlank()) {
                return apiResponse.getMessage();
            }
        } catch (Exception ignored) {
        }
        return "Login failed";
    }
}
