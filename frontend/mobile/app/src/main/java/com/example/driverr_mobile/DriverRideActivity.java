package com.example.driverr_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.RideResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.network.RideApi;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.concurrent.TimeUnit;

public class DriverRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MaterialToolbar toolbar;
    private ScrollView contentScroll;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView noRideMessage;
    private LinearLayout rideDetailsContainer;
    
    private TextView rideStatus;
    private TextView rideId;
    private TextView pickupAddress;
    private TextView destinationAddress;
    private TextView price;
    private TextView passengerCount;
    private TextView createdAt;
    private TextView startedAt;
    
    private Button startRideBtn;
    private Button finishRideBtn;
    
    private GoogleMap googleMap;
    private RideResponse currentRide;
    private String driverId;
    private SessionManager sessionManager;
    private RideApi rideApi;
    
    private Handler pollHandler;
    private Runnable pollRunnable;
    private final long POLL_INTERVAL = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.driver_ride_root), 
            (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

        initViews();
        sessionManager = new SessionManager(this);
        rideApi = ApiClient.getInstance().create(RideApi.class);
        driverId = sessionManager.getUserId();
        
        if (driverId == null || driverId.isEmpty()) {
            showError("Please log in as a driver");
            return;
        }
        
        loadCurrentRide();
        setupMapFragment();
        setupPolling();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        contentScroll = findViewById(R.id.content_scroll);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        noRideMessage = findViewById(R.id.no_ride_message);
        rideDetailsContainer = findViewById(R.id.ride_details_container);
        
        rideStatus = findViewById(R.id.ride_status);
        rideId = findViewById(R.id.ride_id);
        pickupAddress = findViewById(R.id.pickup_address);
        destinationAddress = findViewById(R.id.destination_address);
        price = findViewById(R.id.price);
        passengerCount = findViewById(R.id.passenger_count);
        createdAt = findViewById(R.id.created_at);
        startedAt = findViewById(R.id.started_at);
        
        startRideBtn = findViewById(R.id.start_ride_btn);
        finishRideBtn = findViewById(R.id.finish_ride_btn);
        
        setSupportActionBar(toolbar);
        
        startRideBtn.setOnClickListener(v -> onStartRideClicked());
        finishRideBtn.setOnClickListener(v -> onFinishRideClicked());
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupPolling() {
        pollHandler = new Handler(Looper.getMainLooper());
        pollRunnable = this::loadCurrentRide;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Default center: Novi Sad, Serbia
        LatLng noviSad = new LatLng(45.2671, 19.8335);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noviSad, 13));
        
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        
        updateMapMarkers();
    }

    private void loadCurrentRide() {
        if (isFinishing()) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        rideApi.getDriverCurrentRide(driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        currentRide = response.body();
                        errorMessage.setVisibility(View.GONE);
                        noRideMessage.setVisibility(View.GONE);
                        rideDetailsContainer.setVisibility(View.VISIBLE);
                        displayRideDetails();
                        updateMapMarkers();
                    } else {
                        currentRide = null;
                        rideDetailsContainer.setVisibility(View.GONE);
                        noRideMessage.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    rideDetailsContainer.setVisibility(View.GONE);
                    noRideMessage.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayRideDetails() {
        if (currentRide == null) return;
        
        rideStatus.setText(formatStatusText(currentRide.getStatus()));
        rideStatus.setTextColor(getStatusColor(currentRide.getStatus()));
        
        rideId.setText("Ride #" + currentRide.getRideId().substring(0, Math.min(8, currentRide.getRideId().length())));
        pickupAddress.setText(currentRide.getPickupAddress() != null ? currentRide.getPickupAddress() : "N/A");
        destinationAddress.setText(currentRide.getDestinationAddress() != null ? currentRide.getDestinationAddress() : "N/A");
        price.setText(String.format("RSD %.2f", currentRide.getPrice()));
        
        int passengers = currentRide.getPassengerIds() != null ? currentRide.getPassengerIds().size() : 0;
        passengerCount.setText(String.valueOf(passengers));
        
        createdAt.setText(formatDateTime(currentRide.getCreatedAt()));
        startedAt.setText(currentRide.getStartedAt() != null ? formatDateTime(currentRide.getStartedAt()) : "Not started");
        
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (currentRide == null) return;
        
        String status = currentRide.getStatus();
        
        boolean canStart = "ASSIGNED".equals(status);
        boolean canFinish = "IN_PROGRESS".equals(status);
        
        startRideBtn.setEnabled(canStart);
        startRideBtn.setAlpha(canStart ? 1.0f : 0.5f);
        startRideBtn.setVisibility(View.VISIBLE);
        
        finishRideBtn.setEnabled(canFinish);
        finishRideBtn.setAlpha(canFinish ? 1.0f : 0.5f);
        finishRideBtn.setVisibility(View.VISIBLE);
    }

    private void onStartRideClicked() {
        if (currentRide == null) return;
        
        startRideBtn.setEnabled(false);
        rideApi.startRide(currentRide.getRideId(), driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentRide = response.body();
                        displayRideDetails();
                        updateMapMarkers();
                        Toast.makeText(DriverRideActivity.this, "Ride started! Drive safely.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DriverRideActivity.this, "Failed to start ride", Toast.LENGTH_SHORT).show();
                        startRideBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                if (!isFinishing()) {
                    Toast.makeText(DriverRideActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    startRideBtn.setEnabled(true);
                }
            }
        });
    }

    private void onFinishRideClicked() {
        if (currentRide == null) return;
        
        finishRideBtn.setEnabled(false);
        rideApi.finishRide(currentRide.getRideId(), driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful()) {
                        Toast.makeText(DriverRideActivity.this, "Ride completed!", Toast.LENGTH_SHORT).show();
                        currentRide = null;
                        rideDetailsContainer.setVisibility(View.GONE);
                        noRideMessage.setVisibility(View.VISIBLE);
                        
                        // Reload after 2 seconds
                        new Handler(Looper.getMainLooper()).postDelayed(DriverRideActivity.this::loadCurrentRide, 2000);
                    } else {
                        Toast.makeText(DriverRideActivity.this, "Failed to finish ride", Toast.LENGTH_SHORT).show();
                        finishRideBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                if (!isFinishing()) {
                    Toast.makeText(DriverRideActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finishRideBtn.setEnabled(true);
                }
            }
        });
    }

    private void updateMapMarkers() {
        if (googleMap == null || currentRide == null) return;
        
        googleMap.clear();
        
        // For a complete implementation, you'd parse the address or fetch coordinates
        // For now, we just center on Novi Sad
        LatLng noviSad = new LatLng(45.2671, 19.8335);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noviSad, 13));
    }

    private String formatStatusText(String status) {
        switch (status) {
            case "ASSIGNED":
                return "Assigned - Ready to Start";
            case "IN_PROGRESS":
                return "In Progress";
            case "FINISHED":
                return "Finished";
            default:
                return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "ASSIGNED":
                return getResources().getColor(android.R.color.holo_orange_dark, getTheme());
            case "IN_PROGRESS":
                return getResources().getColor(android.R.color.holo_blue_dark, getTheme());
            case "FINISHED":
                return getResources().getColor(android.R.color.holo_green_dark, getTheme());
            default:
                return getResources().getColor(android.R.color.black, getTheme());
        }
    }

    private String formatDateTime(String dateTimeString) {
        if (dateTimeString == null) return "N/A";
        try {
            return dateTimeString.substring(0, Math.min(16, dateTimeString.length()));
        } catch (Exception e) {
            return dateTimeString;
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        rideDetailsContainer.setVisibility(View.GONE);
        noRideMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start polling
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.postDelayed(pollRunnable, POLL_INTERVAL);
        }
        loadCurrentRide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop polling
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
    }

    private void loadCurrentRide(Runnable callback) {
        loadCurrentRide();
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.postDelayed(pollRunnable, POLL_INTERVAL);
        }
    }
}
