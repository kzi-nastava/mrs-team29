package com.example.driverr_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.UpdateVehiclePricingRequest;
import com.example.driverr_mobile.data.model.VehiclePricing;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.network.AdminApi;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPricingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView errorMessage;

    private TextInputEditText standardBase;
    private TextInputEditText standardPerKm;
    private TextInputEditText luxuryBase;
    private TextInputEditText luxuryPerKm;
    private TextInputEditText vanBase;
    private TextInputEditText vanPerKm;

    private Button saveStandardBtn;
    private Button saveLuxuryBtn;
    private Button saveVanBtn;

    private AdminApi adminApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_pricing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_pricing_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adminApi = ApiClient.getAdminApi();
        initViews();
        loadPricing();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);

        standardBase = findViewById(R.id.standard_base_price);
        standardPerKm = findViewById(R.id.standard_price_per_km);
        luxuryBase = findViewById(R.id.luxury_base_price);
        luxuryPerKm = findViewById(R.id.luxury_price_per_km);
        vanBase = findViewById(R.id.van_base_price);
        vanPerKm = findViewById(R.id.van_price_per_km);

        saveStandardBtn = findViewById(R.id.save_standard_btn);
        saveLuxuryBtn = findViewById(R.id.save_luxury_btn);
        saveVanBtn = findViewById(R.id.save_van_btn);

        saveStandardBtn.setOnClickListener(v -> savePricing("STANDARD", standardBase, standardPerKm));
        saveLuxuryBtn.setOnClickListener(v -> savePricing("LUXURY", luxuryBase, luxuryPerKm));
        saveVanBtn.setOnClickListener(v -> savePricing("VAN", vanBase, vanPerKm));
    }

    private void loadPricing() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);

        adminApi.getAllVehiclePricing().enqueue(new Callback<List<VehiclePricing>>() {
            @Override
            public void onResponse(Call<List<VehiclePricing>> call, Response<List<VehiclePricing>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Failed to load pricing");
                    return;
                }

                for (VehiclePricing pricing : response.body()) {
                    if (pricing.getVehicleType() == null) {
                        continue;
                    }

                    switch (pricing.getVehicleType().toUpperCase(Locale.ROOT)) {
                        case "STANDARD":
                            standardBase.setText(formatDouble(pricing.getBasePrice()));
                            standardPerKm.setText(formatDouble(pricing.getPricePerKm()));
                            break;
                        case "LUXURY":
                            luxuryBase.setText(formatDouble(pricing.getBasePrice()));
                            luxuryPerKm.setText(formatDouble(pricing.getPricePerKm()));
                            break;
                        case "VAN":
                            vanBase.setText(formatDouble(pricing.getBasePrice()));
                            vanPerKm.setText(formatDouble(pricing.getPricePerKm()));
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VehiclePricing>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Failed to load pricing: " + t.getMessage());
            }
        });
    }

    private void savePricing(String vehicleType, TextInputEditText baseInput, TextInputEditText perKmInput) {
        Double basePrice = parseNonNegativeDouble(baseInput);
        Double pricePerKm = parseNonNegativeDouble(perKmInput);

        if (basePrice == null || pricePerKm == null) {
            Toast.makeText(this, "Please enter valid non-negative prices", Toast.LENGTH_SHORT).show();
            return;
        }

        adminApi.updateVehiclePricing(vehicleType, new UpdateVehiclePricingRequest(basePrice, pricePerKm))
            .enqueue(new Callback<VehiclePricing>() {
                @Override
                public void onResponse(Call<VehiclePricing> call, Response<VehiclePricing> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminPricingActivity.this, vehicleType + " pricing updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminPricingActivity.this, "Failed to update " + vehicleType + " pricing", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VehiclePricing> call, Throwable t) {
                    Toast.makeText(AdminPricingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private Double parseNonNegativeDouble(TextInputEditText input) {
        if (input.getText() == null) {
            return null;
        }

        try {
            double value = Double.parseDouble(input.getText().toString().trim());
            return value >= 0 ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDouble(double value) {
        if (Math.floor(value) == value) {
            return String.format(Locale.getDefault(), "%.0f", value);
        }
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }
}
