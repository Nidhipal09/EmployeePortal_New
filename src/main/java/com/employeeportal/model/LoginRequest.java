package com.employeeportal.model;

import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class LoginRequest {

    @Pattern(regexp = "^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})?$", message = "Email must be a valid email address")
    private String email;

    @Pattern(regexp = "^(\\d{10})?$", message = "Mobile number must be valid")
    private String mobileNumber;
    
    private String password;
}

