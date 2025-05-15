package com.employeeportal.controller.registration;

import javax.validation.Valid;
import com.employeeportal.dto.registration.RegistrationRequestDTO;
import com.employeeportal.dto.registration.RegistrationResponseDTO;
import com.employeeportal.dto.registration.SendOtpDto;
import com.employeeportal.dto.registration.TokenDto;
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
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@RequestBody TokenDto tokenDto) {
        ValidateTokenResponseDto validateTokenResponseDto = registrationService.validateToken(tokenDto.getToken());
        if(validateTokenResponseDto.isTokenValid()) {
            return new ResponseEntity<>(validateTokenResponseDto, HttpStatus.OK);
        }
            return new ResponseEntity<>(validateTokenResponseDto, HttpStatus.UNAUTHORIZED);
            
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtpEmail(@RequestBody SendOtpDto email) {
        System.out.println("skjskjskjssssssssjjjjjjjjjjjjjjjjjjj" + email.getEmail());
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
            System.out.println("erroooooooooooooooooooooooooooooooor");
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
    public ResponseEntity<String> resendActivationLink(@RequestParam String email ) {

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        try {
            String activationLink = registrationService.resendActivationLink(email);
            return ResponseEntity.ok("Activation link resent successfully: " + activationLink);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to resend activation link.");
        }
    }

    // @PostMapping("/save")
    // @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    // public ResponseEntity<PrimaryDetails> savePrimaryDetails(@RequestBody
    // PrimaryDetailsDTO primaryDetails) {
    // PrimaryDetails addprimaryDetails =
    // registrationService.savePrimaryDetails(primaryDetails);
    // return new ResponseEntity<>(addprimaryDetails, HttpStatus.CREATED);

    // }

    // @GetMapping("/allPrimaryDetails")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_EMPLOYEE')")
    // public ResponseEntity<List<PrimaryDetails>> getAllPrimaryDetails() {
    // List<PrimaryDetails> allPrimaryDetails =
    // registrationService.getAllPrimaryDetails();
    // return new ResponseEntity<>(allPrimaryDetails, HttpStatus.OK);
    // }

    // @GetMapping("/primary/{primaryId}")
    // @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    // public ResponseEntity<PrimaryDetails>
    // getPrimaryDetailsById(@PathVariable("primaryId") Long primaryId) {
    // PrimaryDetails primaryDetailsById =
    // registrationService.getPrimaryDetailsById(primaryId);

    // if (primaryDetailsById == null) {
    // throw new ResourceNotFoundException("User not found with id " + primaryId);
    // }
    // return new ResponseEntity<>(primaryDetailsById, HttpStatus.OK);
    // }

    // @PutMapping("/update/{primaryId}")
    // public ResponseEntity<PrimaryDetails>
    // updatePrimaryDetailsById(@PathVariable("primaryId") Long primaryId,
    // @RequestBody PrimaryDetails primaryDetails) {
    // PrimaryDetails updatePrimaryDetails =
    // registrationService.updatePrimaryDetailsById(primaryId, primaryDetails);
    // // return ResponseEntity.ok(updateUser);
    // return new ResponseEntity<>(updatePrimaryDetails, HttpStatus.OK);
    // }

    // @DeleteMapping("/delete/{primaryId}")
    // public ResponseEntity<String>
    // deletePrimaryDetailsById(@PathVariable("primaryId") Long primaryId) {
    // PrimaryDetails isDeleted =
    // registrationService.deletePrimaryDetailsById(primaryId);
    // return new ResponseEntity<>("data deleted", HttpStatus.OK);
    // }

}
