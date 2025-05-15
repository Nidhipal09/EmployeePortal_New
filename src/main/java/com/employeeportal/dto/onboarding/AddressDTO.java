package com.employeeportal.dto.onboarding;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class AddressDTO {
    private String addressType;
    private String address1;
    private String address2;
    private String address3;

    @Pattern(regexp = "\\d{6}", message = "Zip code must contain only numbers and be exactly 6 digits")
    private String zip;

    private String state;
    private String city;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String stayFrom;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String stayTo;

    @Pattern(regexp = "\\d{10}", message = "Emergency contact number must be exactly 10 digits and contain only numbers")
    private String emergencyContactNumber;
    private String emergencyContactNameAndRelationship;

}