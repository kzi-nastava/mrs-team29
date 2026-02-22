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

import com.example.driverr_mobile.data.model.AdminRideState;
import com.example.driverr_mobile.data.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminRideMonitorActivity extends AppCompatActivity {

    private TextInputEditText driverNameInput;
    private MaterialButton searchButton;
    private TextView loadingText;
    private TextView emptyState;
    private LinearLayout resultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_ride_monitor);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_ride_monitor_root), (v, insets) -> {
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

        driverNameInput = findViewById(R.id.admin_monitor_driver_name);
        searchButton = findViewById(R.id.admin_monitor_search_button);
        loadingText = findViewById(R.id.admin_monitor_loading_text);
        emptyState = findViewById(R.id.admin_monitor_empty_state);
        resultsContainer = findViewById(R.id.admin_monitor_results_container);

        searchButton.setOnClickListener(v -> loadData());
        loadData();
    }

    private void loadData() {
        String driverName = valueOf(driverNameInput);
        if (driverName.isBlank()) {
            driverName = null;
        }

        loadingText.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        resultsContainer.removeAllViews();
        searchButton.setEnabled(false);

        ApiClient.getAdminApi().getActiveRideMonitor(driverName)
                .enqueue(new retrofit2.Callback<List<AdminRideState>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<AdminRideState>> call,
                                           retrofit2.Response<List<AdminRideState>> response) {
                        loadingText.setVisibility(View.GONE);
                        searchButton.setEnabled(true);

                        if (!response.isSuccessful() || response.body() == null) {
                            showEmpty("Failed to load active rides");
                            return;
                        }

                        List<AdminRideState> rides = response.body();
                        if (rides.isEmpty()) {
                            showEmpty("No active rides found");
                            return;
                        }

                        renderRides(rides);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<AdminRideState>> call, Throwable t) {
                        loadingText.setVisibility(View.GONE);
                        searchButton.setEnabled(true);
                        Toast.makeText(AdminRideMonitorActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmpty("No active rides found");
                    }
                });
    }

    private void renderRides(List<AdminRideState> rides) {
        resultsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AdminRideState ride : rides) {
            View card = inflater.inflate(R.layout.item_admin_active_ride, resultsContainer, false);

            ((TextView) card.findViewById(R.id.item_driver_name))
                    .setText("Driver: " + nullSafe(ride.getDriverName(), "Unknown"));
            ((TextView) card.findViewById(R.id.item_ride_id))
                    .setText("Ride ID: " + nullSafe(ride.getRideId(), "-"));
            ((TextView) card.findViewById(R.id.item_status))
                    .setText("Status: " + nullSafe(ride.getStatus(), "-"));
            ((TextView) card.findViewById(R.id.item_pickup))
                    .setText("Pickup: " + nullSafe(ride.getPickupAddress(), "-"));
            ((TextView) card.findViewById(R.id.item_destination))
                    .setText("Destination: " + nullSafe(ride.getDestinationAddress(), "-"));
            ((TextView) card.findViewById(R.id.item_started))
                    .setText("Started at: " + nullSafe(ride.getStartedAt(), "-"));
            ((TextView) card.findViewById(R.id.item_est_arrival))
                    .setText("Estimated arrival: " + nullSafe(ride.getEstimatedArrival(), "-"));
            ((TextView) card.findViewById(R.id.item_current_position))
                    .setText("Current position: " + ride.getCurrentLatitude() + ", " + ride.getCurrentLongitude()
                            + " (" + nullSafe(ride.getCurrentLocationDescription(), "-") + ")");

            resultsContainer.addView(card);
        }
    }

    private void showEmpty(String message) {
        resultsContainer.removeAllViews();
        emptyState.setText(message);
        emptyState.setVisibility(View.VISIBLE);
    }

    private String valueOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String nullSafe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
