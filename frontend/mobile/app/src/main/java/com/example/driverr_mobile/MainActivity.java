package com.example.driverr_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import com.example.driverr_mobile.data.prefs.SessionManager;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        SessionManager sessionManager = new SessionManager(this);
        configureMenuForRole(navigationView, sessionManager.getRole());

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item);
            drawerLayout.closeDrawers();
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleNavigation(MenuItem item) {
        if (item.getItemId() == R.id.nav_main) {
            return;
        }
        if (item.getItemId() == R.id.nav_order) {
            startActivity(new Intent(this, RideOrderActivity.class));
            return;
        }
        if (item.getItemId() == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoriteRoutesActivity.class));
            return;
        }
        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        if (item.getItemId() == R.id.nav_admin_approvals) {
            startActivity(new Intent(this, AdminApprovalActivity.class));
            return;
        }
        if (item.getItemId() == R.id.nav_logout) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.clear();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String label = item.getTitle() == null ? "" : item.getTitle().toString();
        Toast.makeText(this, label + " (coming soon)", Toast.LENGTH_SHORT).show();
    }

    private void configureMenuForRole(NavigationView navigationView, String role) {
        String effectiveRole = role == null || role.isBlank() ? "CLIENT" : role.toUpperCase();
        Menu menu = navigationView.getMenu();

        setVisible(menu, R.id.nav_main, true);
        setVisible(menu, R.id.nav_logout, true);

        boolean isClient = "CLIENT".equals(effectiveRole);
        boolean isDriver = "DRIVER".equals(effectiveRole);
        boolean isAdmin = "ADMIN".equals(effectiveRole);

        setVisible(menu, R.id.nav_order, isClient);
        setVisible(menu, R.id.nav_favorites, isClient);

        setVisible(menu, R.id.nav_ride, isDriver);
        setVisible(menu, R.id.nav_driver_history, isDriver);

        setVisible(menu, R.id.nav_profile, isClient || isDriver);
        setVisible(menu, R.id.nav_admin_approvals, isAdmin);
    }

    private void setVisible(Menu menu, int itemId, boolean visible) {
        MenuItem item = menu.findItem(itemId);
        if (item != null) {
            item.setVisible(visible);
        }
    }
}