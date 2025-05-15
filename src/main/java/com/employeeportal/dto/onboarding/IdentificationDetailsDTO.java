package com.employeeportal.dto.onboarding;

import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class IdentificationDetailsDTO {

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar Identification Number must be valid")
    private String aadharIdentificationNumber;
    private String aadharIdentificationUrl;

    @Pattern(regexp = "^[A-Z]{5}\\d{4}[A-Z]$", message = "PAN Identification Number must be valid")
    private String panIdentificationNumber;
    private String panIdentificationUrl;
}