package com.employeeportal.service.onboarding;

import com.employeeportal.dto.onboarding.GeneralResponse;
import com.employeeportal.dto.onboarding.OnboardingResponseDTO;
import com.employeeportal.dto.onboarding.PreviewDto;
import com.employeeportal.dto.onboarding.PreviewResponseDTO;
import com.employeeportal.model.onboarding.OnboardingDetails;

public interface OnboardingService {
    OnboardingResponseDTO fillOnboardingDetails(OnboardingDetails onboardingDetails, String email,
            String pageIdentifier);

    OnboardingResponseDTO getOnboardingDetails(String email, String pageIdentifier);

    PreviewResponseDTO getAllOnboardingDetails(String email);

    void addPreviewDetails(String email, PreviewDto previewDto);

    GeneralResponse notifyAdmin(String email);

    String updateEmployeeStatus(String email, String status);
}
