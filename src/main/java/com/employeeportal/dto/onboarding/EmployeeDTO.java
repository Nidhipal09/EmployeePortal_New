package com.employeeportal.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO {
    private int employeeId;
    private String fullName;
    private String mobileNumber;
    private String dateOfBirth;
    private String email;
}