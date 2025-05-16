package com.employeeportal.dto.onboarding;

import com.employeeportal.model.onboarding.OnboardingDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnboardingResponseDTO {

    private OnboardingDetails onboardingDetails;
    private int employeeId;
    private String status;
    private String pageIdentifier;
}
