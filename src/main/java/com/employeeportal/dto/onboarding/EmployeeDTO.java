package com.employeeportal.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO {
    private String fullName;
    private String mobileNumber;
    private String dateOfBirth;
    private String email;
}