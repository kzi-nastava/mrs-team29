package com.example.driverr_mobile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.BlockUserRequest;
import com.example.driverr_mobile.data.model.UserBlockStatus;
import com.example.driverr_mobile.data.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity {

    private final List<UserBlockStatus> users = new ArrayList<>();

    private MaterialToolbar toolbar;
    private LinearLayout userContainer;
    private TextView emptyState;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_management_root), (v, insets) -> {
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

        userContainer = findViewById(R.id.user_container);
        emptyState = findViewById(R.id.empty_state);
        loadingText = findViewById(R.id.loading_text);

        loadUsers();
    }

    private void loadUsers() {
        loadingText.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        userContainer.removeAllViews();

        ApiClient.getAdminApi().getAllUsersBlockStatus()
                .enqueue(new Callback<List<UserBlockStatus>>() {
                    @Override
                    public void onResponse(Call<List<UserBlockStatus>> call,
                                           Response<List<UserBlockStatus>> response) {
                        loadingText.setVisibility(View.GONE);
                        if (!response.isSuccessful() || response.body() == null) {
                            showEmptyState("Failed to load users");
                            return;
                        }

                        users.clear();
                        users.addAll(response.body());

                        if (users.isEmpty()) {
                            showEmptyState("No users found");
                            return;
                        }

                        renderUsers();
                    }

                    @Override
                    public void onFailure(Call<List<UserBlockStatus>> call, Throwable t) {
                        loadingText.setVisibility(View.GONE);
                        showEmptyState("Network error while loading users");
                    }
                });
    }

    private void renderUsers() {
        userContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (UserBlockStatus user : users) {
            View card = inflater.inflate(R.layout.item_user_block, userContainer, false);

            TextView userName = card.findViewById(R.id.user_name);
            TextView userEmail = card.findViewById(R.id.user_email);
            TextView userType = card.findViewById(R.id.user_type);
            TextView userStatus = card.findViewById(R.id.user_status);
            TextView blockNote = card.findViewById(R.id.block_note);
            MaterialButton actionButton = card.findViewById(R.id.action_button);

            userName.setText(user.getFirstName() + " " + user.getLastName());
            userEmail.setText(user.getEmail());
            userType.setText(getUserTypeDisplay(user.getUserType()));
            
            if (user.isBlocked()) {
                userStatus.setText("Blocked");
                userStatus.setTextColor(getColor(R.color.status_blocked));
                blockNote.setText(user.getBlockNote() != null ? user.getBlockNote() : "No note provided");
                blockNote.setVisibility(View.VISIBLE);
                actionButton.setText("Unblock");
                actionButton.setBackgroundColor(getColor(R.color.success_green));
                actionButton.setOnClickListener(v -> showUnblockConfirmation(user));
            } else {
                userStatus.setText("Active");
                userStatus.setTextColor(getColor(R.color.success_green));
                blockNote.setVisibility(View.GONE);
                actionButton.setText("Block");
                actionButton.setBackgroundColor(getColor(R.color.error_red));
                actionButton.setOnClickListener(v -> showBlockDialog(user));
            }

            userContainer.addView(card);
        }
    }

    private void showBlockDialog(UserBlockStatus user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Block User");
        builder.setMessage("You are about to block " + user.getFirstName() + " " + user.getLastName());

        final EditText input = new EditText(this);
        input.setHint("Reason for blocking (required)");
        input.setMinLines(3);
        builder.setView(input);

        builder.setPositiveButton("Block", (dialog, which) -> {
            String blockNote = input.getText().toString().trim();
            if (TextUtils.isEmpty(blockNote)) {
                Toast.makeText(this, "Please provide a reason", Toast.LENGTH_SHORT).show();
                return;
            }
            blockUser(user.getUserId(), blockNote);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showUnblockConfirmation(UserBlockStatus user) {
        new AlertDialog.Builder(this)
                .setTitle("Unblock User")
                .setMessage("Are you sure you want to unblock " + user.getFirstName() + " " + user.getLastName() + "?")
                .setPositiveButton("Unblock", (dialog, which) -> unblockUser(user.getUserId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void blockUser(String userId, String blockNote) {
        BlockUserRequest request = new BlockUserRequest(userId, true, blockNote);
        
        ApiClient.getAdminApi().blockUser(userId, request)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UserManagementActivity.this,
                                    "User blocked successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(UserManagementActivity.this,
                                    "Failed to block user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        Toast.makeText(UserManagementActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unblockUser(String userId) {
        ApiClient.getAdminApi().unblockUser(userId)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UserManagementActivity.this,
                                    "User unblocked successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(UserManagementActivity.this,
                                    "Failed to unblock user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        Toast.makeText(UserManagementActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEmptyState(String message) {
        emptyState.setText(message);
        emptyState.setVisibility(View.VISIBLE);
    }

    private String getUserTypeDisplay(String userType) {
        if (userType == null) return "User";
        return userType.substring(0, 1).toUpperCase() + userType.substring(1).toLowerCase();
    }
}
