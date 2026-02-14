package com.example.driverr_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.ProfileChangeRequest;
import com.example.driverr_mobile.data.network.ApiClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminApprovalActivity extends AppCompatActivity {

    private final List<ProfileChangeRequest> requests = new ArrayList<>();

    private LinearLayout requestContainer;
    private TextView emptyState;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_approval);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_approval_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestContainer = findViewById(R.id.admin_request_container);
        emptyState = findViewById(R.id.admin_empty_state);
        loadingText = findViewById(R.id.admin_loading_text);

        loadRequests();
    }

    private void loadRequests() {
        loadingText.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        requestContainer.removeAllViews();

        ApiClient.getAdminApi().getPendingProfileChangeRequests()
                .enqueue(new retrofit2.Callback<List<ProfileChangeRequest>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ProfileChangeRequest>> call,
                                           retrofit2.Response<List<ProfileChangeRequest>> response) {
                        loadingText.setVisibility(View.GONE);
                        if (!response.isSuccessful() || response.body() == null) {
                            showEmptyState("Failed to load requests");
                            return;
                        }

                        requests.clear();
                        for (ProfileChangeRequest request : response.body()) {
                            if (request.getStatus() == null || "PENDING".equalsIgnoreCase(request.getStatus())) {
                                requests.add(request);
                            }
                        }

                        if (requests.isEmpty()) {
                            showEmptyState("No pending profile change requests");
                            return;
                        }

                        renderRequests();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<ProfileChangeRequest>> call, Throwable t) {
                        loadingText.setVisibility(View.GONE);
                        showEmptyState("Network error while loading requests");
                    }
                });
    }

    private void renderRequests() {
        requestContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ProfileChangeRequest request : requests) {
            View card = inflater.inflate(R.layout.item_admin_request, requestContainer, false);

            TextView driverName = card.findViewById(R.id.admin_request_driver);
            TextView fieldName = card.findViewById(R.id.admin_request_field);
            TextView oldValue = card.findViewById(R.id.admin_request_old_value);
            TextView newValue = card.findViewById(R.id.admin_request_new_value);
            TextView createdAt = card.findViewById(R.id.admin_request_created_at);
            MaterialButton approveButton = card.findViewById(R.id.admin_request_approve);
            MaterialButton rejectButton = card.findViewById(R.id.admin_request_reject);

            driverName.setText(nullToFallback(request.getUserName(), "Unknown"));
            fieldName.setText(nullToFallback(request.getFieldName(), ""));
            oldValue.setText(nullToFallback(request.getOldValue(), "N/A"));
            newValue.setText(nullToFallback(request.getNewValue(), "N/A"));
            createdAt.setText(nullToFallback(request.getCreatedAt(), ""));

            approveButton.setOnClickListener(v -> submitDecision(request.getId(), true));
            rejectButton.setOnClickListener(v -> submitDecision(request.getId(), false));

            requestContainer.addView(card);
        }
    }

    private void submitDecision(String requestId, boolean approve) {
        if (requestId == null || requestId.isBlank()) {
            return;
        }

        retrofit2.Call<Object> call = approve
                ? ApiClient.getAdminApi().approveProfileChangeRequest(requestId)
                : ApiClient.getAdminApi().rejectProfileChangeRequest(requestId);

        call.enqueue(new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(retrofit2.Call<Object> call, retrofit2.Response<Object> response) {
                if (response.isSuccessful()) {
                    String message = approve ? "Request approved" : "Request rejected";
                    Toast.makeText(AdminApprovalActivity.this, message, Toast.LENGTH_SHORT).show();
                    removeRequest(requestId);
                } else {
                    Toast.makeText(AdminApprovalActivity.this, "Action failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                Toast.makeText(AdminApprovalActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeRequest(String requestId) {
        for (int i = 0; i < requests.size(); i++) {
            if (requestId.equals(requests.get(i).getId())) {
                requests.remove(i);
                break;
            }
        }
        if (requests.isEmpty()) {
            showEmptyState("No pending profile change requests");
        } else {
            renderRequests();
        }
    }

    private void showEmptyState(String message) {
        requestContainer.removeAllViews();
        emptyState.setText(message);
        emptyState.setVisibility(View.VISIBLE);
    }

    private String nullToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
