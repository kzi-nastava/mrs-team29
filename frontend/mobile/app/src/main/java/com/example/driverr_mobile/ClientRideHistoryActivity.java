package com.example.driverr_mobile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.RatingRequest;
import com.example.driverr_mobile.data.model.RatingResponse;
import com.example.driverr_mobile.data.model.RideResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientRideHistoryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView emptyText;
    private LinearLayout container;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_ride_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.client_history_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.client_history_progress);
        emptyText = findViewById(R.id.client_history_empty);
        container = findViewById(R.id.client_history_container);

        loadHistory();
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        container.removeAllViews();

        String userId = sessionManager.getUserId();
        ApiClient.getRideApi().getUserRideHistory(userId)
                .enqueue(new Callback<List<RideResponse>>() {
                    @Override
                    public void onResponse(Call<List<RideResponse>> call, Response<List<RideResponse>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (!response.isSuccessful() || response.body() == null) {
                            showEmpty("Failed to load ride history");
                            return;
                        }

                        List<RideResponse> rides = response.body();
                        if (rides.isEmpty()) {
                            showEmpty("No ride history found");
                            return;
                        }

                        renderHistory(rides);
                    }

                    @Override
                    public void onFailure(Call<List<RideResponse>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showEmpty("Network error: " + t.getMessage());
                    }
                });
    }

    private void renderHistory(List<RideResponse> rides) {
        LayoutInflater inflater = LayoutInflater.from(this);
        container.removeAllViews();

        for (RideResponse ride : rides) {
            View card = inflater.inflate(R.layout.item_client_ride_history, container, false);

            TextView route = card.findViewById(R.id.client_item_route);
            TextView driver = card.findViewById(R.id.client_item_driver);
            TextView finished = card.findViewById(R.id.client_item_finished);
            TextView price = card.findViewById(R.id.client_item_price);
            MaterialButton rateBtn = card.findViewById(R.id.client_item_rate_button);

            route.setText("Route: " + nullSafe(ride.getPickupAddress()) + " -> " + nullSafe(ride.getDestinationAddress()));
            driver.setText("Driver: " + nullSafe(ride.getDriverName()));
            finished.setText("Finished: " + nullSafe(ride.getFinishedAt()));
            price.setText("Price: $" + ride.getPrice());

            boolean canRate = canRateRide(ride.getFinishedAt());
            rateBtn.setEnabled(canRate);
            rateBtn.setOnClickListener(v -> showRateDialog(ride));

            if (!canRate) {
                rateBtn.setText("Rating expired");
            }

            container.addView(card);
        }
    }

    private boolean canRateRide(String finishedAt) {
        if (finishedAt == null || finishedAt.isBlank()) {
            return false;
        }
        try {
            LocalDateTime finished = LocalDateTime.parse(finishedAt);
            return LocalDateTime.now().isBefore(finished.plusDays(3));
        } catch (Exception e) {
            return false;
        }
    }

    private void showRateDialog(RideResponse ride) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rate_ride, null);
        TextInputEditText driverInput = dialogView.findViewById(R.id.rate_driver_input);
        TextInputEditText vehicleInput = dialogView.findViewById(R.id.rate_vehicle_input);
        TextInputEditText commentInput = dialogView.findViewById(R.id.rate_comment_input);

        new AlertDialog.Builder(this)
                .setTitle("Rate ride")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> submitRating(ride, driverInput, vehicleInput, commentInput))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitRating(RideResponse ride, TextInputEditText driverInput, TextInputEditText vehicleInput, TextInputEditText commentInput) {
        int driverRating = parseRating(driverInput);
        int vehicleRating = parseRating(vehicleInput);
        if (driverRating < 1 || driverRating > 5 || vehicleRating < 1 || vehicleRating > 5) {
            Toast.makeText(this, "Ratings must be between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = commentInput.getText() == null ? null : commentInput.getText().toString().trim();
        String userId = sessionManager.getUserId();

        RatingRequest request = new RatingRequest(
                ride.getRideId(),
                userId,
                driverRating,
                vehicleRating,
                comment
        );

        ApiClient.getRatingApi().submitRating(request)
                .enqueue(new Callback<RatingResponse>() {
                    @Override
                    public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(ClientRideHistoryActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String status = response.body().getStatus();
                        if ("CREATED".equalsIgnoreCase(status)) {
                            Toast.makeText(ClientRideHistoryActivity.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                        } else if ("ALREADY_RATED".equalsIgnoreCase(status)) {
                            Toast.makeText(ClientRideHistoryActivity.this, "You already rated this ride", Toast.LENGTH_SHORT).show();
                        } else if ("EXPIRED".equalsIgnoreCase(status)) {
                            Toast.makeText(ClientRideHistoryActivity.this, "Rating period expired", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ClientRideHistoryActivity.this, "Rating status: " + status, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RatingResponse> call, Throwable t) {
                        Toast.makeText(ClientRideHistoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int parseRating(TextInputEditText input) {
        try {
            String raw = input.getText() == null ? "" : input.getText().toString().trim();
            return Integer.parseInt(raw);
        } catch (Exception e) {
            return 0;
        }
    }

    private void showEmpty(String message) {
        container.removeAllViews();
        emptyText.setText(message);
        emptyText.setVisibility(View.VISIBLE);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
