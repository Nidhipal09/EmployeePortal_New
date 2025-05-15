package com.employeeportal.dto.registration;

public class ValidateTokenResponseDto {

    private boolean isTokenValid;
    private String email;
    private String message;

    public ValidateTokenResponseDto() {
        super();
    }

    public ValidateTokenResponseDto(boolean isTokenValid, String message, String email) {
        super();
        this.isTokenValid = isTokenValid;
        this.message = message;
        this.email = email;
    }

    public boolean isTokenValid() {
        return isTokenValid;
    }

    public void setTokenValid(boolean isTokenValid) {
        this.isTokenValid = isTokenValid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
