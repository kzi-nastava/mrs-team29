package com.example.driverr_mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private TextView summaryText;
    
    private Button startDateBtn;
    private Button endDateBtn;
    private Button filterBtn;
    private Button clearBtn;
    
    private String driverId;
    private SessionManager sessionManager;
    private RideApi rideApi;
    
    private String startDate = null;
    private String endDate = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

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
        summaryText = findViewById(R.id.summary_text);
        
        startDateBtn = findViewById(R.id.start_date_btn);
        endDateBtn = findViewById(R.id.end_date_btn);
        filterBtn = findViewById(R.id.filter_btn);
        clearBtn = findViewById(R.id.clear_btn);
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        startDateBtn.setOnClickListener(v -> showStartDatePicker());
        endDateBtn.setOnClickListener(v -> showEndDatePicker());
        filterBtn.setOnClickListener(v -> loadRideHistory());
        clearBtn.setOnClickListener(v -> clearDateFilter());
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                startDate = dateFormat.format(calendar.getTime());
                startDateBtn.setText("From: " + displayFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                endDate = dateFormat.format(calendar.getTime());
                endDateBtn.setText("To: " + displayFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void clearDateFilter() {
        startDate = null;
        endDate = null;
        startDateBtn.setText("Select Start Date");
        endDateBtn.setText("Select End Date");
        loadRideHistory();
    }

    private void loadRideHistory() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        
        rideApi.getDriverRideHistory(driverId, startDate, endDate).enqueue(new Callback<List<RideResponse>>() {
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
            summaryText.setVisibility(View.GONE);
            return;
        }
        
        noHistoryMessage.setVisibility(View.GONE);
        rideHistoryContainer.setVisibility(View.VISIBLE);
        summaryText.setVisibility(View.VISIBLE);
        
        // Calculate total earnings
        double totalEarnings = 0;
        for (RideResponse ride : rides) {
            totalEarnings += ride.getPrice();
        }
        
        summaryText.setText(String.format(Locale.getDefault(), 
            "Total Rides: %d  |  Total Earnings: RSD %.2f", rides.size(), totalEarnings));
        
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
        
        // Add bottom margin
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
        String dateStr = ride.getFinishedAt() != null ? ride.getFinishedAt() : ride.getCreatedAt();
        dateView.setText("📅 " + formatDate(dateStr));
        dateView.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        dateView.setTextSize(12);
        itemLayout.addView(dateView);
        
        // Route
        TextView routeView = new TextView(this);
        String pickup = ride.getPickupAddress() != null ? ride.getPickupAddress() : "N/A";
        String dest = ride.getDestinationAddress() != null ? ride.getDestinationAddress() : "N/A";
        routeView.setText(pickup + " → " + dest);
        routeView.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
        routeView.setTextSize(14);
        routeView.setPadding(0, 8, 0, 0);
        itemLayout.addView(routeView);
        
        // Driver name (if available)
        if (ride.getDriverName() != null && !ride.getDriverName().isEmpty()) {
            TextView driverView = new TextView(this);
            driverView.setText("👤 Driver: " + ride.getDriverName());
            driverView.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            driverView.setTextSize(12);
            driverView.setPadding(0, 4, 0, 0);
            itemLayout.addView(driverView);
        }
        
        // Price, Passengers and Status
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setPadding(0, 8, 0, 0);
        
        TextView priceView = new TextView(this);
        priceView.setText("💰 RSD " + String.format(Locale.getDefault(), "%.2f", ride.getPrice()));
        priceView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        priceView.setTypeface(null, android.graphics.Typeface.BOLD);
        priceView.setTextSize(14);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        priceView.setLayoutParams(priceParams);
        infoLayout.addView(priceView);
        
        TextView passengersView = new TextView(this);
        int passengerCount = ride.getPassengerIds() != null ? ride.getPassengerIds().size() : 0;
        passengersView.setText("👥 " + passengerCount);
        passengersView.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        passengersView.setTextSize(12);
        passengersView.setPadding(16, 0, 0, 0);
        infoLayout.addView(passengersView);
        
        TextView statusView = new TextView(this);
        statusView.setText(" ✅ Finished");
        statusView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        statusView.setTextSize(12);
        statusView.setPadding(16, 0, 0, 0);
        infoLayout.addView(statusView);
        
        itemLayout.addView(infoLayout);
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
        summaryText.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRideHistory();
    }
}
