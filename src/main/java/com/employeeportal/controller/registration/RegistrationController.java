package com.employeeportal.controller.registration;

import javax.validation.Valid;

import com.employeeportal.dto.onboarding.GeneralResponse;
import com.employeeportal.dto.registration.RegistrationRequestDTO;
import com.employeeportal.dto.registration.RegistrationResponseDTO;
import com.employeeportal.dto.registration.SendOtpDto;
import com.employeeportal.dto.registration.ValidateOtpDto;
import com.employeeportal.dto.registration.ValidateTokenResponseDto;
import com.employeeportal.exception.NotFoundException;
import com.employeeportal.model.OtpResponse;
import com.employeeportal.model.login.ResponseDTO;
import com.employeeportal.model.registration.ValidateOtpTokenResponse;
import com.employeeportal.util.JwtUtil;
import com.employeeportal.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.employeeportal.service.registration.RegistrationService;

@RestController
@RequestMapping("/primaryDetails")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RegistrationController {
    private final ResponseUtil responseUtil;
    private final JwtUtil jwtUtil;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(ResponseUtil responseUtil, JwtUtil jwtUtil,
            RegistrationService registrationService) {
        this.responseUtil = responseUtil;
        this.jwtUtil = jwtUtil;
        this.registrationService = registrationService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<RegistrationResponseDTO> registerEmployee(@RequestBody @Valid RegistrationRequestDTO employeeRegistrationDTO) {
        RegistrationResponseDTO registrationResponseDTO = registrationService.registerEmployee(employeeRegistrationDTO);
        return new ResponseEntity<>(registrationResponseDTO, HttpStatus.CREATED);

    }

    @PostMapping("/validateToken")
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@RequestParam String token) {
        ValidateTokenResponseDto validateTokenResponseDto = registrationService.validateToken(token);
        if(validateTokenResponseDto.isTokenValid()) {
            return new ResponseEntity<>(validateTokenResponseDto, HttpStatus.OK);
        }
            return new ResponseEntity<>(validateTokenResponseDto, HttpStatus.UNAUTHORIZED);
            
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtpEmail(@RequestBody SendOtpDto email) {
        String otp = registrationService.sendOtpEmail(email.getEmail());
        return ResponseEntity.ok(new OtpResponse(otp));
    }

    @PostMapping("/validate")
    public ResponseEntity<ResponseDTO> validateOtp(@RequestParam String token, @RequestParam String otp) {
        try {
            ValidateOtpDto validateOtpDto = registrationService.validateOtp(token, otp);
            ValidateOtpTokenResponse validateResponse = new ValidateOtpTokenResponse();

            
            if (validateOtpDto.isOtpValid()) {
                validateResponse.setToken(jwtUtil.generateToken(validateOtpDto.getEmail()));
                validateResponse.setEmail(validateOtpDto.getEmail());
            } else {
                throw new NotFoundException();
            }
            ResponseDTO response = responseUtil.prepareResponseDto(validateResponse,
                    "Otp is Valid",
                    HttpStatus.OK.value(), 
                    true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ResponseDTO response = responseUtil.prepareResponseDto(null,
                    "Invalid or expired OTP",
                    HttpStatus.BAD_REQUEST.value(), 
                    false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<?> resendOtp(@RequestBody SendOtpDto email) {
        String otp = registrationService.sendOtpEmail(email.getEmail());
        return ResponseEntity.ok(new OtpResponse(otp));
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<?> resendActivationLink(@RequestParam String email ) {

        if (email == null || email.isEmpty()) {
            GeneralResponse response = new GeneralResponse("Email is required.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            String activationLink = registrationService.resendActivationLink(email);
            GeneralResponse response = new GeneralResponse("Activation link resent successfully: " + activationLink);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            GeneralResponse response = new GeneralResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            GeneralResponse response = new GeneralResponse("Failed to resend activation link.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
