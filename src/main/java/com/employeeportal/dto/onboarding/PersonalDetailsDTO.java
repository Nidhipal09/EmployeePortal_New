package com.employeeportal.dto.onboarding;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PersonalDetailsDTO {
    private String imageUrl;
    private String gender;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String motherName;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String fatherName;

    @Pattern(regexp = "\\d{10}", message = "Secondary mobile must be exactly 10 digits")
    private String secondaryMobile;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Full name must contain only letters and spaces")
    private String fullName;

    @JsonIgnore
    public boolean isNull() {
        return this == null ||
                (this.imageUrl == null &&
                        this.gender == null &&
                        this.motherName == null &&
                        this.fatherName == null &&
                        this.secondaryMobile == null &&
                        this.fullName == null);
    }
}