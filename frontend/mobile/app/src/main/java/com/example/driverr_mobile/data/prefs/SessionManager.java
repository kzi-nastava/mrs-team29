package com.example.driverr_mobile.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.driverr_mobile.data.model.LoginResponse;

public class SessionManager {
    private static final String PREFS_NAME = "driverr_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "user_email";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuth(LoginResponse response) {
        prefs.edit()
                .putString(KEY_TOKEN, response.getToken())
                .putString(KEY_USER_ID, response.getUserId())
                .putString(KEY_EMAIL, response.getEmail())
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
