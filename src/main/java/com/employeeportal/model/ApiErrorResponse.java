package com.employeeportal.model;

public class ApiErrorResponse extends GeneralResponse<String> {

    private int statusCode;

    public ApiErrorResponse(String message, int statusCode) {
        super(false, message, null);
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
