package com.employeeportal.controller.onboarding;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employeeportal.dto.onboarding.GeneralResponse;
import com.employeeportal.dto.onboarding.OnboardingResponseDTO;
import com.employeeportal.dto.onboarding.PreviewDto;
import com.employeeportal.dto.onboarding.PreviewResponseDTO;
import com.employeeportal.dto.onboarding.UpdateStatusRequest;
import com.employeeportal.model.onboarding.OnboardingDetails;
import com.employeeportal.service.onboarding.OnboardingService;;

@RestController
@RequestMapping("/onboarding")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OnboardingResponseDTO> fillOnboardingDetails(
            @RequestBody @Valid OnboardingDetails onboardingDetails,
            @RequestParam String email, @RequestParam String pageIdentifier) {
        OnboardingResponseDTO onboardingResponseDTO = onboardingService.fillOnboardingDetails(onboardingDetails, email,
                pageIdentifier);
        return new ResponseEntity<>(onboardingResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OnboardingResponseDTO> getOnboardingDetails(@RequestParam String email,
            @RequestParam String pageIdentifier) {
        OnboardingResponseDTO onboardingResponseDTO = onboardingService.getOnboardingDetails(email, pageIdentifier);
        return new ResponseEntity<>(onboardingResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/preview")
    @PreAuthorize("isAuthenticated()")
    // for preview
    public ResponseEntity<PreviewResponseDTO> getAllOnboardingDetails(@RequestParam String email) {
        System.out.println("email" + email);
        PreviewResponseDTO previewResponseDTO = onboardingService.getAllOnboardingDetails(email);
        System.out.println(previewResponseDTO.toString());
        return new ResponseEntity<>(previewResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/preview")
    @PreAuthorize("isAuthenticated()")
    // for preview
    public ResponseEntity<GeneralResponse> AddPreviewDetails(@RequestParam String email, @RequestBody PreviewDto previewDto) {
        onboardingService.addPreviewDetails(email, previewDto);
        return new ResponseEntity<>(new GeneralResponse("All details are submitted successfully."), HttpStatus.OK);
    }


    @PostMapping("/notifyAdmin")
    @PreAuthorize("isAuthenticated()")
    // for preview
    public ResponseEntity<GeneralResponse> notifyAdmin(@RequestParam String email) {
        GeneralResponse generalResponse = onboardingService.notifyAdmin(email);
        return new ResponseEntity<>(generalResponse, HttpStatus.OK);
    }

    // Update employee status
    @PutMapping("/updateStatus")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<GeneralResponse> updateEmployeeStatus(@RequestParam String email,
            @RequestBody UpdateStatusRequest updateStatusRequest) {
        String message = onboardingService.updateEmployeeStatus(email, updateStatusRequest.getStatus());
        return new ResponseEntity<>(new GeneralResponse(message), HttpStatus.OK);
    }
}
