package com.employeeportal.service.registration;

import com.employeeportal.dto.registration.RegistrationRequestDTO;
import com.employeeportal.dto.registration.RegistrationResponseDTO;
import com.employeeportal.dto.registration.ValidateOtpDto;
import com.employeeportal.dto.registration.ValidateTokenResponseDto;

public interface RegistrationService {

	RegistrationResponseDTO registerEmployee(RegistrationRequestDTO registrationRequest);

	String sendOtpEmail(String email);

	ValidateOtpDto validateOtp(String token, String otp);

	String resendActivationLink(String email);

	ValidateTokenResponseDto validateToken(String token);
}