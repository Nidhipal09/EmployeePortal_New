package com.employeeportal.dto.registration;

import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class RegistrationRequest {

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must be a valid email address")
    private String email;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Full name must contain only letters and spaces")
    private String fullName;

    @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits and contain only numbers")
    private String mobileNumber;

    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$", message = "Date must be in the format dd/MM/yyyy")
    private String dateOfBirth;
    private String status;

}
