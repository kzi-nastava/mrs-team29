package com.example.driverr_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.InconsistencyNoteRequest;
import com.example.driverr_mobile.data.model.RideResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class PassengerRideTrackingActivity extends AppCompatActivity {

    private TextView statusText, pickupText, destinationText, etaText;
    private MaterialButton reportButton, refreshButton;
    private String userId;
    private Handler handler;
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passenger_ride_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.passenger_ride_tracking_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        statusText = findViewById(R.id.ride_tracking_status);
        pickupText = findViewById(R.id.ride_tracking_pickup);
        destinationText = findViewById(R.id.ride_tracking_destination);
        etaText = findViewById(R.id.ride_tracking_eta);
        reportButton = findViewById(R.id.ride_tracking_report_button);
        refreshButton = findViewById(R.id.ride_tracking_refresh_button);

        if (getIntent() != null && getIntent().getData() != null) {
            String rideId = getIntent().getData().getQueryParameter("rideId");
            if (rideId != null && !rideId.isBlank()) {
                statusText.setText("Opening ride tracking...");
            }
        }

        reportButton.setOnClickListener(v -> showReportDialog());
        refreshButton.setOnClickListener(v -> loadRideStatus());

        handler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadRideStatus();
                handler.postDelayed(this, 10000); // Refresh every 10 seconds
            }
        };

        loadRideStatus();
        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    private void loadRideStatus() {
        ApiClient.getRideApi().getUserCurrentRide(userId)
                .enqueue(new retrofit2.Callback<RideResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<RideResponse> call,
                                           retrofit2.Response<RideResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayRideInfo(response.body());
                        } else {
                            statusText.setText("No active ride");
                            reportButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<RideResponse> call, Throwable t) {
                        Toast.makeText(PassengerRideTrackingActivity.this, "Failed to load ride info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayRideInfo(RideResponse ride) {
        statusText.setText("Status: " + ride.getStatus());
        pickupText.setText("Pickup: " + ride.getPickupAddress());
        destinationText.setText("Destination: " + ride.getDestinationAddress());

        // Calculate simple ETA (stub - would need real-time location updates)
        if ("IN_PROGRESS".equals(ride.getStatus())) {
            etaText.setText("ETA: Calculating...");
            reportButton.setEnabled(true);
        } else {
            etaText.setText("Ride not yet started");
            reportButton.setEnabled(false);
        }
    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report driver inconsistency");

        final TextInputEditText input = new TextInputEditText(this);
        input.setHint("Describe the issue");
        input.setMinLines(3);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String noteText = input.getText() == null ? "" : input.getText().toString().trim();
            if (!noteText.isEmpty()) {
                submitInconsistencyReport(noteText);
            } else {
                Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void submitInconsistencyReport(String noteText) {
        // Get current ride ID from latest active ride
        ApiClient.getRideApi().getUserRideHistory(userId)
                .enqueue(new retrofit2.Callback<java.util.List<RideResponse>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<RideResponse>> call,
                                           retrofit2.Response<java.util.List<RideResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RideResponse activeRide = null;
                            for (RideResponse ride : response.body()) {
                                if ("IN_PROGRESS".equals(ride.getStatus())) {
                                    activeRide = ride;
                                    break;
                                }
                            }

                            if (activeRide != null) {
                                InconsistencyNoteRequest request = new InconsistencyNoteRequest(activeRide.getRideId(), noteText);
                                ApiClient.getRideApi().reportInconsistency(request)
                                        .enqueue(new retrofit2.Callback<com.example.driverr_mobile.data.model.InconsistencyNoteResponse>() {
                                            @Override
                                            public void onResponse(retrofit2.Call<com.example.driverr_mobile.data.model.InconsistencyNoteResponse> call,
                                                                   retrofit2.Response<com.example.driverr_mobile.data.model.InconsistencyNoteResponse> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(PassengerRideTrackingActivity.this,
                                                            "Inconsistency reported", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(PassengerRideTrackingActivity.this,
                                                            "Failed to submit report", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(retrofit2.Call<com.example.driverr_mobile.data.model.InconsistencyNoteResponse> call, Throwable t) {
                                                Toast.makeText(PassengerRideTrackingActivity.this,
                                                        "Network error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(PassengerRideTrackingActivity.this,
                                        "No active ride to report", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<RideResponse>> call, Throwable t) {
                        Toast.makeText(PassengerRideTrackingActivity.this,
                                "Failed to get ride info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
