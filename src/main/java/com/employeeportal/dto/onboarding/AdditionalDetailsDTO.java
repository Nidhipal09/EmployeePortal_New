package com.employeeportal.dto.onboarding;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AdditionalDetailsDTO {
    private String illnessDeclaration;
    private String hobbiesDeclaration;

    @JsonIgnore
    public boolean isNull() {
        return this == null ||
                (this.illnessDeclaration == null &&
                        this.hobbiesDeclaration == null);
    }
}