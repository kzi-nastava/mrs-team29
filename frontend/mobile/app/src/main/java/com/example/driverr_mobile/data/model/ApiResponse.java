package com.example.driverr_mobile.data.model;

public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;

    public ApiResponse() {}

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
