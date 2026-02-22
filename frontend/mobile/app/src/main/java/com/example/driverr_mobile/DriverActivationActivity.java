package com.example.driverr_mobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.DriverActivationRequest;
import com.example.driverr_mobile.data.network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class DriverActivationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_activation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.driver_activation_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText passwordInput = findViewById(R.id.driver_activation_password);
        TextInputEditText confirmPasswordInput = findViewById(R.id.driver_activation_confirm_password);
        MaterialButton activateButton = findViewById(R.id.driver_activation_button);
        TextView messageView = findViewById(R.id.driver_activation_message);
        TextView footerView = findViewById(R.id.driver_activation_footer);

        Uri data = getIntent() == null ? null : getIntent().getData();
        String token = data == null ? null : data.getQueryParameter("token");

        if (token == null || token.isBlank()) {
            messageView.setText("Activation token is missing from the link.");
            activateButton.setEnabled(false);
        }

        footerView.setOnClickListener(v -> finish());

        activateButton.setOnClickListener(v -> {
            String password = textOf(passwordInput);
            String confirmPassword = textOf(confirmPasswordInput);

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                messageView.setText("Please fill in both password fields.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                messageView.setText("Passwords do not match.");
                return;
            }
            if (token == null || token.isBlank()) {
                messageView.setText("Activation token is missing from the link.");
                return;
            }

            activateButton.setEnabled(false);
            activateButton.setText("Activating...");

            DriverActivationRequest request = new DriverActivationRequest(token, password, confirmPassword);
            ApiClient.getDriverApi().activateDriver(request)
                    .enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ApiResponse<Object>> call,
                                               retrofit2.Response<ApiResponse<Object>> response) {
                            activateButton.setEnabled(true);
                            activateButton.setText("Activate account");

                            if (response.isSuccessful()) {
                                String message = response.body() != null && response.body().getMessage() != null
                                        ? response.body().getMessage()
                                        : "Driver account activated successfully";
                                Toast.makeText(DriverActivationActivity.this, message, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                String errorMessage = extractErrorMessage(response);
                                messageView.setText(errorMessage);
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) {
                            activateButton.setEnabled(true);
                            activateButton.setText("Activate account");
                            messageView.setText("Network error. Please try again.");
                        }
                    });
        });
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        if (response.errorBody() == null) {
            return "Activation failed";
        }
        try {
            String body = response.errorBody().string();
            ApiResponse<?> apiResponse = new Gson().fromJson(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getMessage() != null && !apiResponse.getMessage().isBlank()) {
                return apiResponse.getMessage();
            }
        } catch (Exception ignored) {
        }
        return "Activation failed";
    }
}
