package com.example.driverr_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.AddressResponse;
import com.example.driverr_mobile.data.model.GeocodeRequest;
import com.example.driverr_mobile.data.model.RideOrderRequest;
import com.example.driverr_mobile.data.model.RideResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RideOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText pickupInput;
    private TextInputEditText destinationInput;
    private TextView pickupSelected;
    private TextView destinationSelected;
    private TextInputEditText passengersInput;
    private MaterialAutoCompleteTextView vehicleTypeInput;
    private TextInputEditText scheduleInput;
    private TextInputEditText notesInput;
    private MaterialCheckBox petsCheckbox;
    private MaterialCheckBox babyCheckbox;
    private MaterialButton orderButton;
    private TextView activeRideWarning;

    private String creatorId;
    private String pickupAddressId;
    private String destinationAddressId;
    
    private GoogleMap googleMap;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private AddressResponse pickupAddressData;
    private AddressResponse destinationAddressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order_ride_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pickupInput = findViewById(R.id.order_pickup_input);
        destinationInput = findViewById(R.id.order_destination_input);
        pickupSelected = findViewById(R.id.order_pickup_selected);
        destinationSelected = findViewById(R.id.order_destination_selected);
        passengersInput = findViewById(R.id.order_passengers_input);
        vehicleTypeInput = findViewById(R.id.order_vehicle_type);
        scheduleInput = findViewById(R.id.order_schedule_input);
        notesInput = findViewById(R.id.order_notes_input);
        petsCheckbox = findViewById(R.id.order_pets_checkbox);
        babyCheckbox = findViewById(R.id.order_baby_checkbox);
        orderButton = findViewById(R.id.order_submit_button);
        activeRideWarning = findViewById(R.id.order_active_warning);

        SessionManager sessionManager = new SessionManager(this);
        creatorId = sessionManager.getUserId();

        if (creatorId == null || creatorId.isBlank()) {
            Toast.makeText(this, "Please log in to order a ride.", Toast.LENGTH_LONG).show();
            finish();
            return;
        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.order_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        checkActiveRide();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Default center: Novi Sad, Serbia
        LatLng noviSad = new LatLng(45.2671, 19.8335);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noviSad, 13));
        
        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Set map click listener
        googleMap.setOnMapClickListener(latLng -> {
            reverseGeocodeLocation(latLng);
        }

        findViewById(R.id.order_pickup_find).setOnClickListener(v -> geocodeAddress(true));
        findViewById(R.id.order_destination_find).setOnClickListener(v -> geocodeAddress(false));
        orderButton.setOnClickListener(v -> submitOrder());

        checkActiveRide();
    }

    private void checkActiveRide() {
        ApiClient.getRideApi().hasActiveRide(creatorId)
                .enqueue(new retrofit2.Callback<Boolean>() {
                    @Override
                    public void onResponse(retrofit2.Call<Boolean> call, retrofit2.Response<Boolean> response) {
                        boolean hasActive = response.isSuccessful() && Boolean.TRUE.equals(response.body());
                        activeRideWarning.setVisibility(hasActive ? View.VISIBLE : View.GONE);
                        orderButton.setEnabled(!hasActive);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Boolean> call, Throwable t) {
                        activeRideWarning.setVisibility(View.GONE);
                        orderButton.setEnabled(true);
                    }
                });
    }AddressData = address;
                            pickupSelected.setText(display);
                            addPickupMarker(new LatLng(address.getLatitude(), address.getLongitude()), display);
                        } else {
                            destinationAddressId = address.getId();
                            destinationAddressData = address;
                            destinationSelected.setText(display);
                            addDestinationMarker(new LatLng(address.getLatitude(), address.getLongitude()), nput : destinationInput;
        String query = textOf(sourceInput);
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter an address to search", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ApiClient.getAddressApi().geocodeAndSave(new GeocodeRequest(query))
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        setLoading(false);
                 reverseGeocodeLocation(LatLng latLng) {
        // Reverse geocode using Nominatim API
        // Format: reverse geocode by lat/lng to get address
        String query = latLng.latitude + "," + latLng.longitude;
        
        setLoading(true);
        ApiClient.getAddressApi().geocodeAndSave(new GeocodeRequest(query))
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        setLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(RideOrderActivity.this, "Could not get address for this location", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AddressResponse address = response.body();
                        String display = address.getDisplayName();
                        if (display == null || display.isBlank()) {
                            display = address.getStreet() + " " + address.getStreetNumber();
                        }

                        // Assign to pickup if not set, otherwise to destination
                        if (pickupAddressId == null || pickupAddressId.isBlank()) {
                            pickupAddressId = address.getId();
                            pickupAddressData = address;
                            pickupSelected.setText(display);
                            addPickupMarker(latLng, display);
                        } else if (destinationAddressId == null || destinationAddressId.isBlank()) {
                            destinationAddressId = address.getId();
                            destinationAddressData = address;
                            destinationSelected.setText(display);
                            addDestinationMarker(latLng, display);
                        } else {
                            // Both are set, replace destination
                            destinationAddressId = address.getId();
                            destinationAddressData = address;
                            destinationSelected.setText(display);
                            addDestinationMarker(latLng, display);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AddressResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RideOrderActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPickupMarker(LatLng position, String title) {
        if (googleMap == null) return;
        
        // Remove old pickup marker
        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        
        // Add green marker for pickup
        pickupMarker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Pickup: " + title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        
        // Move camera to marker
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    private void addDestinationMarker(LatLng position, String title) {
        if (googleMap == null) return;
        
        // Remove old destination marker
        pickupAddressData = null;
        destinationAddressData = null;
        
        // Clear markers
        if (pickupMarker != null) {
            pickupMarker.remove();
            pickupMarker = null;
        }
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        
        // Add red marker for destination
        destinationMarker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Destination: " + title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        
        // Move camera to show both markers if pickup exists
        if (pickupMarker != null) {
            // Zoom to show both markers
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    private void        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(RideOrderActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AddressResponse address = response.body();
                        String display = address.getDisplayName();
                        if (display == null || display.isBlank()) {
                            display = address.getStreet() + " " + address.getStreetNumber();
                        }

                        if (pickup) {
                            pickupAddressId = address.getId();
                            pickupSelected.setText(display);
                        } else {
                            destinationAddressId = address.getId();
                            destinationSelected.setText(display);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AddressResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RideOrderActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitOrder() {
        if (creatorId == null || creatorId.isBlank()) {
            Toast.makeText(this, "Please log in to order a ride.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickupAddressId == null || pickupAddressId.isBlank()) {
            Toast.makeText(this, "Pick-up address is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destinationAddressId == null || destinationAddressId.isBlank()) {
            Toast.makeText(this, "Destination address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String passengers = textOf(passengersInput);
        List<String> passengerIds = parseCommaList(passengers);
        String vehicleType = textOf(vehicleTypeInput);
        String scheduledTime = textOf(scheduleInput);
        String notes = textOf(notesInput);

        RideOrderRequest request = new RideOrderRequest(
                creatorId,
                pickupAddressId,
                destinationAddressId,
                new ArrayList<>(),
                passengerIds,
                vehicleType.isEmpty() ? "STANDARD" : vehicleType,
                petsCheckbox.isChecked(),
                babyCheckbox.isChecked(),
                scheduledTime.isEmpty() ? null : scheduledTime,
                notes.isEmpty() ? null : notes
        );

        setLoading(true);
        ApiClient.getRideApi().orderRide(request)
                .enqueue(new retrofit2.Callback<RideResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<RideResponse> call,
                                           retrofit2.Response<RideResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            RideResponse ride = response.body();
                            Toast.makeText(RideOrderActivity.this,
                                    "Ride ordered! Price: " + ride.getPrice() + " RSD",
                                    Toast.LENGTH_LONG).show();
                            resetForm();
                            checkActiveRide();
                        } else {
                            Toast.makeText(RideOrderActivity.this, extractErrorMessage(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<RideResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RideOrderActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetForm() {
        pickupInput.setText("");
        destinationInput.setText("");
        pickupSelected.setText("Not selected");
        destinationSelected.setText("Not selected");
        passengersInput.setText("");
        scheduleInput.setText("");
        notesInput.setText("");
        vehicleTypeInput.setText("STANDARD", false);
        petsCheckbox.setChecked(false);
        babyCheckbox.setChecked(false);
        pickupAddressId = null;
        destinationAddressId = null;
    }

    private void setLoading(boolean loading) {
        orderButton.setEnabled(!loading);
        orderButton.setText(loading ? "Ordering..." : "Order Ride");
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String textOf(MaterialAutoCompleteTextView input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private List<String> parseCommaList(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        List<String> items = new ArrayList<>();
        for (String item : Arrays.asList(value.split(","))) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }
        return items;
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        if (response.errorBody() == null) {
            return "Failed to order ride";
        }
        try {
            return response.errorBody().string();
        } catch (Exception e) {
            return "Failed to order ride";
        }
    }
}
