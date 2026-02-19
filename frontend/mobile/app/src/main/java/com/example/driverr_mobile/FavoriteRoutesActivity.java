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

import com.example.driverr_mobile.data.model.AddressResponse;
import com.example.driverr_mobile.data.model.FavoriteRideOrderRequest;
import com.example.driverr_mobile.data.model.FavoriteRoute;
import com.example.driverr_mobile.data.model.GeocodeRequest;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteRoutesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView messageText;
    private TextView errorText;
    private TextView warningText;
    private TextView loadingText;
    private LinearLayout listContainer;
    private LinearLayout formContainer;

    private TextInputEditText nameInput;
    private TextInputEditText pickupInput;
    private TextInputEditText destinationInput;
    private TextInputEditText stopsInput;
    private TextView pickupSelected;
    private TextView destinationSelected;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton toggleButton;

    private String userId;
    private boolean hasActiveRide;
    private String editingId;
    private String pickupAddressId;
    private String destinationAddressId;
    
    private GoogleMap googleMap;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private AddressResponse pickupAddressData;
    private AddressResponse destinationAddressData;

    private final List<FavoriteRoute> favoriteRoutes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_routes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorite_routes_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        messageText = findViewById(R.id.favorite_message);
        errorText = findViewById(R.id.favorite_error);
        warningText = findViewById(R.id.favorite_warning);
        loadingText = findViewById(R.id.favorite_loading);
        listContainer = findViewById(R.id.favorite_list_container);
        formContainer = findViewById(R.id.favorite_form_container);

        nameInput = findViewById(R.id.favorite_name_input);
        pickupInput = findViewById(R.id.favorite_pickup_input);
        destinationInput = findViewById(R.id.favorite_destination_input);
        stopsInput = findViewById(R.id.favorite_stops_input);
        pickupSelected = findViewById(R.id.favorite_pickup_selected);
        destinationSelected = findViewById(R.id.favorite_destination_selected);
        saveButton = findViewById(R.id.favorite_save_button);
        cancelButton = findViewById(R.id.favorite_cancel_button);
        toggleButton = findViewById(R.id.favorite_toggle_button);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null || userId.isBlank()) {
            Toast.makeText(this, "Please log in to manage favorite routes.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.favorite_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        formContainer.setVisibility(View.GONE);
        loadFavoriteRoutes();
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
        });
        findViewById(R.id.favorite_save_button).setOnClickListener(v -> saveFavoriteRoute());
        findViewById(R.id.favorite_pickup_find).setOnClickListener(v -> geocodeAddress(true));
        findViewById(R.id.favorite_destination_find).setOnClickListener(v -> geocodeAddress(false));
        toggleButton.setOnClickListener(v -> toggleForm());
        cancelButton.setOnClickListener(v -> closeForm());

        formContainer.setVisibility(View.GONE);
        loadFavoriteRoutes();
        checkActiveRide();
    }

    private void loadFavoriteRoutes() {
        loadingText.setVisibility(View.VISIBLE);
        listContainer.removeAllViews();
        ApiClient.getFavoriteRouteApi().getMyFavorites(userId)
                .enqueue(new retrofit2.Callback<List<FavoriteRoute>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<FavoriteRoute>> call,
                                           retrofit2.Response<List<FavoriteRoute>> response) {
                        loadingText.setVisibility(View.GONE);
                        favoriteRoutes.clear();
                        if (!response.isSuccessful() || response.body() == null) {
                            showError("Failed to load favorite routes");
                            return;
                        }
                        favoriteRoutes.addAll(response.body());
                        renderFavoriteRoutes();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<FavoriteRoute>> call, Throwable t) {
                        loadingText.setVisibility(View.GONE);
                        showError("Network error while loading routes");
                    }
                });
    }

    private void checkActiveRide() {
        ApiClient.getRideApi().hasActiveRide(userId)
                .enqueue(new retrofit2.Callback<Boolean>() {
                    @Override
                    public void onResponse(retrofit2.Call<Boolean> call, retrofit2.Response<Boolean> response) {
                        hasActiveRide = response.isSuccessful() && Boolean.TRUE.equals(response.body());
                        warningText.setVisibility(hasActiveRide ? View.VISIBLE : View.GONE);
                        renderFavoriteRoutes();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Boolean> call, Throwable t) {
                        hasActiveRide = false;
                        warningText.setVisibility(View.GONE);
                    }
                });
    }

    private void renderFavoriteRoutes() {
        listContainer.removeAllViews();
        if (favoriteRoutes.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No favorite routes yet.");
            listContainer.addView(empty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (FavoriteRoute route : favoriteRoutes) {
            View card = inflater.inflate(R.layout.item_favorite_route, listContainer, false);

            TextView name = card.findViewById(R.id.favorite_item_name);
            TextView pickup = card.findViewById(R.id.favorite_item_pickup);
            TextView destination = card.findViewById(R.id.favorite_item_destination);
            TextView stops = card.findViewById(R.id.favorite_item_stops);
            MaterialButton orderButton = card.findViewById(R.id.favorite_item_order);
            MaterialButton editButton = card.findViewById(R.id.favorite_item_edit);
            MaterialButton deleteButton = card.findViewById(R.id.favorite_item_delete);

            name.setText(route.getName());
            pickup.setText("Pickup: " + nullToFallback(route.getPickupAddressId(), "Unknown"));
            destination.setText("Destination: " + nullToFallback(route.getDestinationAddressId(), "Unknown"));
            stops.setText(route.getStopAddressIds() == null || route.getStopAddressIds().isEmpty()
                    ? "Stops: —" : "Stops: " + route.getStopAddressIds().size());

            if (route.getPickupAddressId() != null) {
                fetchAddressLabel(route.getPickupAddressId(), pickup, "Pickup");
            }
            if (route.getDestinationAddressId() != null) {
                fetchAddressLabel(route.getDestinationAddressId(), destination, "Destination");
            }

            orderButton.setEnabled(!hasActiveRide);
            orderButton.setOnClickListener(v -> orderFromFavorite(route.getId()));
            editButton.setOnClickListener(v -> editRoute(route));
            deleteButton.setOnClickListener(v -> deleteRoute(route.getId()));

            listContainer.addView(card);
        }
    }

    private void fetchAddressLabel(String addressId, TextView target, String prefix) {
        ApiClient.getAddressApi().getAddress(addressId)
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }
                        AddressResponse address = response.body();
                        String label = address.getStreet() != null
                                ? address.getStreet() + " " + address.getStreetNumber()
                                : address.getDisplayName();
                        if (label != null && !label.isBlank()) {
                            target.setText(prefix + ": " + label);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AddressResponse> call, Throwable t) {
                        // ignore
                    }
                });
    }

    private void toggleForm() {
        if (formContainer.getVisibility() == View.VISIBLE) {
            closeForm();
        } else {
            openForm();
        }
    }

    private void openForm() {
        formContainer.setVisibility(View.VISIBLE);
        toggleButton.setText("Cancel");
    }

    private void closeForm() {
        formContainer.setVisibility(View.GONE);
        toggleButton.setText("+ Add New Route");
        clearForm();
    }

    private void clearForm() {
        editingId = null;
        nameInput.setText("");
        pickupInput.setText("");
        destinationInput.setText("");
        stopsInput.setText("");
        pickupSelected.setText("Not selected");
        destinationSelected.setText("Not selected");
        pickupAddressId = null;
        destinationAddressId = null;
        pickupAddressData = null;
        destinationAddressData = null;
        hideMessages();
        
        // Clear markers
        if (pickupMarker != null) {
            pickupMarker.remove();
            pickupMarker = null;
        }
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
    }

    private void geocodeAddress(boolean pickup) {
        TextInputEditText sourceInput = pickup ? pickupInput : destinationInput;
        String query = textOf(sourceInput);
        if (query.isEmpty()) {
            showError((pickup ? "Pickup" : "Destination") + " address is required");
            return;
        }

        hideMessages();
        setSaving(true);
        ApiClient.getAddressApi().geocodeAndSave(new GeocodeRequest(query))
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        setSaving(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            showError("Address not found");
                            return;
                        }
                        AddressResponse address = response.body();
                        String display = address.getDisplayName();
                        if (display == null || display.isBlank()) {
                            display = address.getStreet() + " " + address.getStreetNumber();
                        }
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if (pickup) {
                            pickupAddressId = address.getId();
                            pickupAddressData = address;
                            pickupSelected.setText(display);
                            addPickupMarker(latLng, display);
                        } else {
                            destinationAddressId = address.getId();
                            destinationAddressData = address;
                            destinationSelected.setText(display);
                            addDestinationMarker(latLng, display);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AddressResponse> call, Throwable t) {
                        setSaving(false);
                        showError("Network error while searching address");
                    }
                });
    }

    private void saveFavoriteRoute() {
        hideMessages();
        String name = textOf(nameInput);
        if (name.isEmpty()) {
            showError("Route name is required");
            return;
        }
        if (pickupAddressId == null || pickupAddressId.isBlank()) {
            showError("Pickup address is required");
            return;
        }
        if (destinationAddressId == null || destinationAddressId.isBlank()) {
            showError("Destination address is required");
            return;
        }

        List<String> stopAddresses = parseCommaList(textOf(stopsInput));
        setSaving(true);

        if (stopAddresses.isEmpty()) {
            submitFavoriteRoute(new ArrayList<>());
        } else {
            geocodeStops(stopAddresses, new ArrayList<>());
        }
    }

    private void geocodeStops(List<String> stops, List<String> stopIds) {
        if (stops.isEmpty()) {
            submitFavoriteRoute(stopIds);
            return;
        }
        String next = stops.remove(0);
        ApiClient.getAddressApi().geocodeAndSave(new GeocodeRequest(next))
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            setSaving(false);
                            showError("Failed to save stop: " + next);
                            return;
                        }
                        stopIds.add(response.body().getId());
                        geocodeStops(stops, stopIds);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AddressResponse> call, Throwable t) {
                        setSaving(false);
                        showError("Network error while saving stops");
                    }
                });
    }

    private void submitFavoriteRoute(List<String> stopIds) {
        FavoriteRoute route = new FavoriteRoute();
        route.setName(textOf(nameInput));
        route.setUserId(userId);
        route.setPickupAddressId(pickupAddressId);
        route.setDestinationAddressId(destinationAddressId);
        route.setStopAddressIds(stopIds);

        retrofit2.Call<FavoriteRoute> call = editingId == null
                ? ApiClient.getFavoriteRouteApi().createFavorite(route)
                : ApiClient.getFavoriteRouteApi().updateFavorite(editingId, route);

        call.enqueue(new retrofit2.Callback<FavoriteRoute>() {
            @Override
            public void onResponse(retrofit2.Call<FavoriteRoute> call,
                                   retrofit2.Response<FavoriteRoute> response) {
                setSaving(false);
                if (!response.isSuccessful()) {
                    showError("Failed to save favorite route");
                    return;
                }
                showMessage(editingId == null ? "Favorite route created" : "Favorite route updated");
                closeForm();
                loadFavoriteRoutes();
            }

            @Override
            public void onFailure(retrofit2.Call<FavoriteRoute> call, Throwable t) {
                setSaving(false);
                showError("Network error while saving route");
            }
        });
    }

    private void editRoute(FavoriteRoute route) {
        editingId = route.getId();
        openForm();
        nameInput.setText(route.getName());
        pickupAddressId = route.getPickupAddressId();
        destinationAddressId = route.getDestinationAddressId();
        pickupSelected.setText(nullToFallback(route.getPickupAddressId(), "Not selected"));
        destinationSelected.setText(nullToFallback(route.getDestinationAddressId(), "Not selected"));
        stopsInput.setText(route.getStopAddressIds() == null ? "" : String.join(", ", route.getStopAddressIds()));
    }

    private void deleteRoute(String routeId) {
        ApiClient.getFavoriteRouteApi().deleteFavorite(routeId)
                .enqueue(new retrofit2.Callback<Object>() {
                    @Override
                    public void onResponse(retrofit2.Call<Object> call, retrofit2.Response<Object> response) {
                        if (response.isSuccessful()) {
                            showMessage("Route deleted");
                            loadFavoriteRoutes();
                        } else {
                            showError("Failed to delete route");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                        showError("Network error while deleting route");
                    }
                });
    }

    private void orderFromFavorite(String routeId) {
        if (hasActiveRide) {
            showError("You have an active ride. Complete it before ordering another.");
            return;
        }
        ApiClient.getFavoriteRouteApi().orderFromFavorite(routeId, new FavoriteRideOrderRequest(userId))
                .enqueue(new retrofit2.Callback<Object>() {
                    @Override
                    public void onResponse(retrofit2.Call<Object> call, retrofit2.Response<Object> response) {
                        if (response.isSuccessful()) {
                            showMessage("Ride ordered from favorite route!");
                            checkActiveRide();
                        } else {
                            showError("Failed to order from favorite route");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                        showError("Network error while ordering ride");
                    }
                });
    }

    private void reverseGeocodeLocation(LatLng latLng) {
        // Reverse geocode using Nominatim API
        String query = latLng.latitude + "," + latLng.longitude;
        
        setSaving(true);
        ApiClient.getAddressApi().geocodeAndSave(new GeocodeRequest(query))
                .enqueue(new retrofit2.Callback<AddressResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<AddressResponse> call,
                                           retrofit2.Response<AddressResponse> response) {
                        setSaving(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(FavoriteRoutesActivity.this, "Could not get address for this location", Toast.LENGTH_SHORT).show();
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
                        setSaving(false);
                        Toast.makeText(FavoriteRoutesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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

    private void showMessage(String message) {
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        messageText.setVisibility(View.GONE);
    }

    private void hideMessages() {
        messageText.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }

    private void setSaving(boolean saving) {
        saveButton.setEnabled(!saving);
        saveButton.setText(saving ? "Saving..." : (editingId == null ? "Create Route" : "Update Route"));
    }

    private String textOf(TextInputEditText input) {
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

    private String nullToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
