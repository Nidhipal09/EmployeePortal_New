package com.employeeportal.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String fullName;
    private String email;
    private String roleName;
    private String token;
}
