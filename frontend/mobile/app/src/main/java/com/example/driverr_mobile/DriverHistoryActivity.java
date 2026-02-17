package com.example.driverr_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.RideResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.network.RideApi;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHistoryActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ScrollView contentScroll;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private LinearLayout rideHistoryContainer;
    private TextView noHistoryMessage;
    
    private String driverId;
    private SessionManager sessionManager;
    private RideApi rideApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.driver_history_root),
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
        
        loadRideHistory();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        contentScroll = findViewById(R.id.content_scroll);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        rideHistoryContainer = findViewById(R.id.ride_history_container);
        noHistoryMessage = findViewById(R.id.no_history_message);
        
        setSupportActionBar(toolbar);
    }

    private void loadRideHistory() {
        progressBar.setVisibility(View.VISIBLE);
        
        rideApi.getDriverRideHistory(driverId).enqueue(new Callback<List<RideResponse>>() {
            @Override
            public void onResponse(Call<List<RideResponse>> call, Response<List<RideResponse>> response) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        List<RideResponse> rides = response.body();
                        displayRideHistory(rides);
                        errorMessage.setVisibility(View.GONE);
                    } else {
                        showError("Failed to load ride history");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RideResponse>> call, Throwable t) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    showError("Error: " + t.getMessage());
                }
            }
        });
    }

    private void displayRideHistory(List<RideResponse> rides) {
        rideHistoryContainer.removeAllViews();
        
        if (rides == null || rides.isEmpty()) {
            noHistoryMessage.setVisibility(View.VISIBLE);
            rideHistoryContainer.setVisibility(View.GONE);
            return;
        }
        
        noHistoryMessage.setVisibility(View.GONE);
        rideHistoryContainer.setVisibility(View.VISIBLE);
        
        for (RideResponse ride : rides) {
            View rideItem = createRideItemView(ride);
            rideHistoryContainer.addView(rideItem);
        }
    }

    private View createRideItemView(RideResponse ride) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 12, 16, 12);
        itemLayout.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
        
        // Add bottom margin by wrapping in a parent with margin
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wrapperParams = (LinearLayout.LayoutParams) wrapper.getLayoutParams();
        wrapperParams.setMargins(0, 0, 0, 8);
        wrapper.setLayoutParams(wrapperParams);
        
        // Date
        TextView dateView = new TextView(this);
        dateView.setText("📅 " + formatDate(ride.getCreatedAt()));
        dateView.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        dateView.setTextSize(12);
        itemLayout.addView(dateView);
        
        // Route
        TextView routeView = new TextView(this);
        routeView.setText(ride.getPickupAddress() + " → " + ride.getDestinationAddress());
        routeView.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
        routeView.setTextSize(14);
        routeView.setPadding(0, 8, 0, 0);
        itemLayout.addView(routeView);
        
        // Price and Status
        LinearLayout priceStatusLayout = new LinearLayout(this);
        priceStatusLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        priceStatusLayout.setOrientation(LinearLayout.HORIZONTAL);
        priceStatusLayout.setPadding(0, 8, 0, 0);
        
        TextView priceView = new TextView(this);
        priceView.setText("💰 RSD " + String.format("%.2f", ride.getPrice()));
        priceView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        priceView.setTextStyle(android.graphics.Typeface.BOLD);
        priceView.setTextSize(14);
        priceView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        priceStatusLayout.addView(priceView);
        
        TextView statusView = new TextView(this);
        statusView.setText(" ✅ Finished");
        statusView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        statusView.setTextSize(12);
        priceStatusLayout.addView(statusView);
        
        itemLayout.addView(priceStatusLayout);
        wrapper.addView(itemLayout);
        
        return wrapper;
    }

    private String formatDate(String dateTimeString) {
        if (dateTimeString == null) return "N/A";
        try {
            return dateTimeString.substring(0, Math.min(10, dateTimeString.length()));
        } catch (Exception e) {
            return dateTimeString;
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        rideHistoryContainer.setVisibility(View.GONE);
        noHistoryMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRideHistory();
    }
}
