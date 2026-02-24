package com.example.driverr_mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverr_mobile.data.model.NotificationResponse;
import com.example.driverr_mobile.data.network.ApiClient;
import com.example.driverr_mobile.data.prefs.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView emptyText;
    private LinearLayout container;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notifications_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.notifications_progress);
        emptyText = findViewById(R.id.notifications_empty);
        container = findViewById(R.id.notifications_container);

        loadNotifications();
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        container.removeAllViews();

        String userId = sessionManager.getUserId();
        ApiClient.getNotificationApi().getUserNotifications(userId)
                .enqueue(new Callback<List<NotificationResponse>>() {
                    @Override
                    public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (!response.isSuccessful() || response.body() == null) {
                            showEmpty("Failed to load notifications");
                            return;
                        }

                        List<NotificationResponse> notifications = response.body();
                        if (notifications.isEmpty()) {
                            showEmpty("No notifications");
                            return;
                        }

                        renderNotifications(notifications);
                    }

                    @Override
                    public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showEmpty("Network error: " + t.getMessage());
                    }
                });
    }

    private void renderNotifications(List<NotificationResponse> notifications) {
        LayoutInflater inflater = LayoutInflater.from(this);
        container.removeAllViews();

        for (NotificationResponse notification : notifications) {
            View card = inflater.inflate(R.layout.item_notification, container, false);

            TextView title = card.findViewById(R.id.notification_title);
            TextView message = card.findViewById(R.id.notification_message);
            TextView time = card.findViewById(R.id.notification_time);

            title.setText(nullSafe(notification.getTitle(), "Notification"));
            message.setText(nullSafe(notification.getMessage(), ""));
            time.setText(nullSafe(notification.getCreatedAt(), ""));

            if (!notification.isRead()) {
                title.setText("(New) " + title.getText());
            }

            card.setOnClickListener(v -> {
                markAsRead(notification);
                if (notification.getRideId() != null && !notification.getRideId().isBlank()) {
                    Intent intent = new Intent(NotificationsActivity.this, PassengerRideTrackingActivity.class);
                    intent.setData(Uri.parse("driverr://track-ride?rideId=" + notification.getRideId()));
                    startActivity(intent);
                }
            });

            container.addView(card);
        }
    }

    private void markAsRead(NotificationResponse notification) {
        if (notification.isRead() || notification.getId() == null) {
            return;
        }
        ApiClient.getNotificationApi().markAsRead(notification.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            notificationReadToast();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(NotificationsActivity.this, "Failed to mark as read", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void notificationReadToast() {
        Toast.makeText(this, "Notification opened", Toast.LENGTH_SHORT).show();
    }

    private void showEmpty(String message) {
        container.removeAllViews();
        emptyText.setText(message);
        emptyText.setVisibility(View.VISIBLE);
    }

    private String nullSafe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
