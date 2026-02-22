package com.example.driverr_mobile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.ChangePasswordRequest;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton updatePasswordButton;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_password_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        oldPasswordInput = findViewById(R.id.change_password_old);
        newPasswordInput = findViewById(R.id.change_password_new);
        confirmPasswordInput = findViewById(R.id.change_password_confirm);
        updatePasswordButton = findViewById(R.id.change_password_update_button);

        userId = new SessionManager(this).getUserId();
        if (userId == null || userId.isBlank()) {
            Toast.makeText(this, "User not loaded. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        updatePasswordButton.setOnClickListener(v -> submitPasswordChange());
    }

    private void submitPasswordChange() {
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
            .enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<Object>> call, retrofit2.Response<ApiResponse<Object>> response) {
                    updatePasswordButton.setEnabled(true);
                    updatePasswordButton.setText("Update password");

                    if (response.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, extractErrorMessage(response), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) {
                    updatePasswordButton.setEnabled(true);
                    updatePasswordButton.setText("Update password");
                    Toast.makeText(ChangePasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
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
