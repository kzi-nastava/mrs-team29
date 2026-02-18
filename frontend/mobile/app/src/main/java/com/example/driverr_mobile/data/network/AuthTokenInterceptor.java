package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.DriverrApp;
import com.example.driverr_mobile.data.prefs.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthTokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String path = original.url().encodedPath();

        if (path.contains("/api/auth/")) {
            return chain.proceed(original);
        }

        SessionManager sessionManager = new SessionManager(DriverrApp.getAppContext());
        String token = sessionManager.getToken();

        if (token == null || token.isBlank()) {
            return chain.proceed(original);
        }

        Request requestWithAuth = original.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(requestWithAuth);
    }
}
