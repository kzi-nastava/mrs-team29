package com.example.driverr_mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.DailyRideMetric;
import com.example.driverr_mobile.data.model.RideReportResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideReportsActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private TextInputEditText startDateInput;
    private TextInputEditText endDateInput;
    private TextInputEditText scopeInput;
    private TextInputEditText userEmailInput;

    private MaterialButton loadButton;

    private TextView roleHint;
    private LinearLayout adminFilters;
    private TextView rangeText;
    private TextView totalRides;
    private TextView totalKm;
    private TextView totalAmount;
    private TextView avgRides;
    private TextView avgKm;
    private TextView avgAmount;
    private LinearLayout dailyContainer;

    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reports_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        isAdmin = "ADMIN".equalsIgnoreCase(sessionManager.getRole());

        bindViews();
        setupToolbar();
        setupInputs();
        setupRoleUI();

        setDefaultDates();
        loadReport();
    }

    private void bindViews() {
        roleHint = findViewById(R.id.reports_role_hint);
        adminFilters = findViewById(R.id.reports_admin_filters);

        startDateInput = findViewById(R.id.reports_start_date);
        endDateInput = findViewById(R.id.reports_end_date);
        scopeInput = findViewById(R.id.reports_scope_input);
        userEmailInput = findViewById(R.id.reports_user_email_input);

        loadButton = findViewById(R.id.reports_load_button);

        rangeText = findViewById(R.id.reports_range_text);
        totalRides = findViewById(R.id.reports_total_rides);
        totalKm = findViewById(R.id.reports_total_km);
        totalAmount = findViewById(R.id.reports_total_amount);
        avgRides = findViewById(R.id.reports_avg_rides);
        avgKm = findViewById(R.id.reports_avg_km);
        avgAmount = findViewById(R.id.reports_avg_amount);
        dailyContainer = findViewById(R.id.reports_daily_container);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.reports_toolbar);
        toolbar.setNavigationIcon(com.google.android.material.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupInputs() {
        startDateInput.setOnClickListener(v -> showDatePicker(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePicker(endDateInput));
        loadButton.setOnClickListener(v -> loadReport());
    }

    private void setupRoleUI() {
        if (isAdmin) {
            roleHint.setText("Admin report");
            adminFilters.setVisibility(View.VISIBLE);
            if (scopeInput.getText() == null || scopeInput.getText().toString().isBlank()) {
                scopeInput.setText("DRIVER");
            }
        } else {
            roleHint.setText("My ride report");
            adminFilters.setVisibility(View.GONE);
        }
    }

    private void setDefaultDates() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        startDateInput.setText(start.toString());
        endDateInput.setText(end.toString());
    }

    private void showDatePicker(TextInputEditText target) {
        LocalDate base;
        try {
            String value = target.getText() == null ? "" : target.getText().toString();
            base = value.isBlank() ? LocalDate.now() : LocalDate.parse(value);
        } catch (Exception e) {
            base = LocalDate.now();
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                    target.setText(selected.toString());
                },
                base.getYear(),
                base.getMonthValue() - 1,
                base.getDayOfMonth()
        );
        dialog.show();
    }

    private void loadReport() {
        String startDate = textOf(startDateInput);
        String endDate = textOf(endDateInput);

        if (startDate.isBlank() || endDate.isBlank()) {
            Toast.makeText(this, "Please select start and end date", Toast.LENGTH_SHORT).show();
            return;
        }

        loadButton.setEnabled(false);

        if (isAdmin) {
            String scope = textOf(scopeInput).toUpperCase(Locale.ROOT);
            if (!"CLIENT".equals(scope) && !"DRIVER".equals(scope)) {
                Toast.makeText(this, "Scope must be CLIENT or DRIVER", Toast.LENGTH_SHORT).show();
                loadButton.setEnabled(true);
                return;
            }
            String selectedUserEmail = textOf(userEmailInput);
            if (selectedUserEmail.isBlank()) {
                selectedUserEmail = null;
            } else {
                selectedUserEmail = selectedUserEmail.toLowerCase(Locale.ROOT);
            }

            ApiClient.getReportApi()
                    .getAdminReport(scope, selectedUserEmail, startDate, endDate)
                    .enqueue(new Callback<RideReportResponse>() {
                        @Override
                        public void onResponse(Call<RideReportResponse> call, Response<RideReportResponse> response) {
                            loadButton.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null) {
                                renderReport(response.body());
                            } else {
                                showApiError(response, "Failed to load admin report");
                            }
                        }

                        @Override
                        public void onFailure(Call<RideReportResponse> call, Throwable t) {
                            loadButton.setEnabled(true);
                            Toast.makeText(RideReportsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return;
        }

        String userId = sessionManager.getUserId();
        ApiClient.getReportApi()
                .getUserReport(userId, startDate, endDate)
                .enqueue(new Callback<RideReportResponse>() {
                    @Override
                    public void onResponse(Call<RideReportResponse> call, Response<RideReportResponse> response) {
                        loadButton.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            renderReport(response.body());
                        } else {
                            showApiError(response, "Failed to load report");
                        }
                    }

                    @Override
                    public void onFailure(Call<RideReportResponse> call, Throwable t) {
                        loadButton.setEnabled(true);
                        Toast.makeText(RideReportsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderReport(RideReportResponse report) {
        rangeText.setText("Range: " + report.getStartDate() + " - " + report.getEndDate());
        totalRides.setText("Total rides: " + report.getTotalRides());
        totalKm.setText("Total kilometers: " + formatDouble(report.getTotalKilometers()));
        totalAmount.setText("Total amount: " + formatMoney(report.getTotalAmount()));

        avgRides.setText("Avg rides/day: " + formatDouble(report.getAverageRidesPerDay()));
        avgKm.setText("Avg kilometers/day: " + formatDouble(report.getAverageKilometersPerDay()));
        avgAmount.setText("Avg amount/day: " + formatMoney(report.getAverageAmountPerDay()));

        renderDailyChart(report.getDaily());
    }

    private void renderDailyChart(List<DailyRideMetric> days) {
        dailyContainer.removeAllViews();
        if (days == null || days.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No data in selected range");
            dailyContainer.addView(empty);
            return;
        }

        int maxRides = 1;
        double maxKm = 1.0;
        double maxAmount = 1.0;
        for (DailyRideMetric day : days) {
            if (day.getRidesCount() > maxRides) {
                maxRides = day.getRidesCount();
            }
            if (day.getKilometers() > maxKm) {
                maxKm = day.getKilometers();
            }
            if (day.getAmount() > maxAmount) {
                maxAmount = day.getAmount();
            }
        }

        for (DailyRideMetric day : days) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(0, 0, 0, 20);

            TextView dayTitle = new TextView(this);
            dayTitle.setText(day.getDate() + "  (rides: " + day.getRidesCount() + ", km: " + formatDouble(day.getKilometers()) + ", amount: " + formatMoney(day.getAmount()) + ")");
            dayTitle.setTextSize(13);
            card.addView(dayTitle);

            card.addView(createBar("Rides", percentage(day.getRidesCount(), maxRides)));
            card.addView(createBar("Kilometers", percentage(day.getKilometers(), maxKm)));
            card.addView(createBar("Amount", percentage(day.getAmount(), maxAmount)));

            dailyContainer.addView(card);
        }
    }

    private LinearLayout createBar(String label, int percent) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText(label + " " + percent + "%");
        title.setTextSize(12);

        LinearProgressIndicator bar = new LinearProgressIndicator(this, null, com.google.android.material.R.attr.linearProgressIndicatorStyle);
        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgressCompat(percent, false);

        wrap.addView(title);
        wrap.addView(bar);
        return wrap;
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private int percentage(double value, double max) {
        if (max <= 0) {
            return 0;
        }
        return (int) Math.round((value / max) * 100.0);
    }

    private String formatDouble(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String formatMoney(double value) {
        return "$" + String.format(Locale.US, "%.2f", value);
    }

    private void showApiError(Response<RideReportResponse> response, String fallbackMessage) {
        String message = fallbackMessage;
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject obj = new JSONObject(raw);
                if (obj.has("message")) {
                    message = obj.getString("message");
                }
            }
        } catch (Exception ignored) {
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
