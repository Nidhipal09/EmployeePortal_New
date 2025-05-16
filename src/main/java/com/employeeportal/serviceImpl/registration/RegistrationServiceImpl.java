package com.employeeportal.serviceImpl.registration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.employeeportal.exception.AlreadyExistsException;
import com.employeeportal.exception.EncryptionException;
import com.employeeportal.exception.MobileNumberAlreadyExistsException;
import com.employeeportal.model.onboarding.OnboardingDetails;
import com.employeeportal.model.onboarding.PersonalDetails;
import com.employeeportal.model.onboarding.Role;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.onboarding.PersonalDetailsRepository;
import com.employeeportal.repository.onboarding.RoleRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.util.EncryptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.employeeportal.config.EmailConstant;
import com.employeeportal.dto.registration.RegistrationRequestDTO;
import com.employeeportal.dto.registration.RegistrationResponseDTO;
import com.employeeportal.dto.registration.ValidateOtpDto;
import com.employeeportal.dto.registration.ValidateTokenResponseDto;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.registration.RegistrationService;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final EmployeeRepository employeeRepository;

    private final PersonalDetailsRepository personalDetailsRepository;

    private final RoleRepository roleRepository;

    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long OTP_EXPIRATION_TIME = 2; // 2 minutes

    @Autowired
    public RegistrationServiceImpl(EmployeeRepository employeeRepository,
            EmailService emailService, PasswordEncoder passwordEncoder, RedisTemplate<String, Object> redisTemplate,
            PersonalDetailsRepository personalDetailsRepository, RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.personalDetailsRepository = personalDetailsRepository;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.roleRepository = roleRepository;
    }

    public RegistrationResponseDTO registerEmployee(RegistrationRequestDTO employeeRegistrationDTO) {

        if (personalDetailsRepository.existsByPersonalEmail(employeeRegistrationDTO.getEmail()))
            throw new AlreadyExistsException("Email already exists, kindly use a different one.");

        if (employeeRepository.findByMobileNumber(employeeRegistrationDTO.getMobileNumber()).isPresent())
            throw new MobileNumberAlreadyExistsException(
                    "Employee with this mobile number already exists, kindly use a different one.");

        System.out.println("1111111111111");
        Employee employee = new Employee();

        String fullName = employeeRegistrationDTO.getFullName();

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1) {
            employee.setFirstName(parts[0]);
        } else if (parts.length == 2) {
            employee.setFirstName(parts[0]);
            employee.setLastName(parts[1]);
        } else {
            employee.setFirstName(parts[0]);
            employee.setLastName(parts[parts.length - 1]);
            employee.setMiddleName(String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1)));
        }

        employee.setEmail(employeeRegistrationDTO.getEmail());
        System.out.println(employeeRegistrationDTO.toString());
        employee.setMobileNumber(employeeRegistrationDTO.getMobileNumber());
        employee.setDateOfBirth(employeeRegistrationDTO.getDateOfBirth());
        employee.setStatus("PENDING");
        String currentTimeStampString = LocalDateTime.now().toString();
        employee.setCreatedTimeStamp(currentTimeStampString);

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setPersonalEmail(employee.getEmail());

        employee.setPersonalDetails(personalDetails);
        personalDetails.setEmployee(employee);

        EmployeeReg employeeReg = new EmployeeReg();
        employeeReg.setEmail(employeeRegistrationDTO.getEmail());
        employeeReg.setPassword("temporary password");

        Role role = roleRepository.findByRoleName("EMPLOYEE");
        employeeReg.setRole(role);

        employee.setEmployeeReg(employeeReg);
        employeeReg.setEmployee(employee);

        employeeRepository.save(employee);

        System.out.println("22222222222222");

        // String token = UUID.randomUUID().toString();
        String tokenString = employeeRegistrationDTO.getEmail() + "|" + employeeRegistrationDTO.getMobileNumber() + "|"
                + currentTimeStampString;
        String encryptedToken = null;
        try {
            encryptedToken = EncryptionUtil.encrypt(tokenString);
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting token: " + e.getMessage());
        }

        emailService.sendEmail(employee.getEmail(), encryptedToken,
                EmailConstant.SIGN_UP_LINK_SUBJECT, EmailConstant.SIGN_UP_LINK_TEMPLATE_NAME);

        return new RegistrationResponseDTO(employeeRegistrationDTO.getEmail(),
                employeeRegistrationDTO.getFullName(), employeeRegistrationDTO.getMobileNumber(),
                employeeRegistrationDTO.getDateOfBirth(), "PENDING");
    }

    @Override
    public String sendOtpEmail(String email) {
        // Generate a random 6 digit number for OTP
        Random random = new Random();
        String otp = String.valueOf(100000 + random.nextInt(900000));

        // Store OTP in Redis with 2-minute expiration
        redisTemplate.opsForValue().set(email, otp, OTP_EXPIRATION_TIME,
                TimeUnit.MINUTES);

        System.out.println("ooooooooooooooooooooooooooooooooooooooo" + redisTemplate.opsForValue().get(email));

        emailService.sendEmail(email, otp, EmailConstant.SIGN_UP_OTP_SUBJECT,
                EmailConstant.SIGN_UP_OTP_TEMPLATE_NAME);

        return otp; // Return the OTP
    }

    public ValidateOtpDto validateOtp(String token, String otp) {
        String decryptedToken;
        try {
            decryptedToken = EncryptionUtil.decrypt(token);
        } catch (Exception e) {
            throw new EncryptionException("Error decrypting token: " + e.getMessage());
        }

        System.out.println(token + " " + decryptedToken);
        // Split the decrypted token to get email and mobile number
        String[] parts = decryptedToken.split("\\|");

        String email = parts[0];

        String cachedOtp = (String) redisTemplate.opsForValue().get(email);
        System.out.println("Retrieved OTP from Redis for " + email + ": " + cachedOtp + " " + otp);

        if (cachedOtp != null && cachedOtp.equals(otp)) {
            System.out.println("jkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" + redisTemplate.opsForValue().get(email));
            redisTemplate.delete(email);
            System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjj" + redisTemplate.opsForValue().get(email));

            createOnboardingObj(email);
            
            return new ValidateOtpDto(email, true);
        }
        return new ValidateOtpDto(email, false);
    }

    public void createOnboardingObj(String email) {
        OnboardingDetails onboardingDetails = new OnboardingDetails();
        String onboardingDetailsJson = null;
        try {
            onboardingDetailsJson = new ObjectMapper().writeValueAsString(onboardingDetails);
            System.out.println("iiiiiiiiiiiiiiiiii"+onboardingDetailsJson);
        } catch (JsonProcessingException e) {
            System.out.println("errrrrrrrrrrror"+e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        redisTemplate.opsForValue().set("onboarding:" + email, onboardingDetailsJson);
    }

    @Override
    public String resendActivationLink(String email) {

        Employee employee = employeeRepository.findByEmail(email);
        String currentTimeStampString = LocalDateTime.now().toString();

        // String token = UUID.randomUUID().toString();
        String tokenString = email + "|" + employee.getMobileNumber() + "|"
                + currentTimeStampString;
        String encryptedToken = null;
        try {
            encryptedToken = EncryptionUtil.encrypt(tokenString);
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting token: " + e.getMessage());
        }

        // Generate a new activation link with a unique identifier or timestamp
        String activationLink = EmailConstant.ACTIVE_SIGNUP_LINK + "?token=" + encryptedToken;

        // Sending the email with the activation link
        emailService.sendEmail(email, encryptedToken, EmailConstant.RESEND_LINK_SUBJECT,
                EmailConstant.SIGN_UP_LINK_TEMPLATE_NAME);

        return activationLink; // Return the new activation link or a success message
    }

    @Override
    public ValidateTokenResponseDto validateToken(String token) {
        // Decrypt the token
        String decryptedToken;
        try {
            decryptedToken = EncryptionUtil.decrypt(token);
        } catch (Exception e) {
            throw new EncryptionException("Error decrypting token: " + e.getMessage());
        }

        System.out.println(token + " " + decryptedToken);
        // Split the decrypted token to get email and mobile number
        String[] parts = decryptedToken.split("\\|");

        String email = parts[0];
        String mobileNumber = parts[1];
        String timeStamp = parts[2];

        if (parts.length != 3) {
            return new ValidateTokenResponseDto(false, "Invalid token", email); // Invalid token format
        }

        Employee employee = employeeRepository.findByEmail(email);

        if (employee == null) {
            return new ValidateTokenResponseDto(false, "Invalid token", email);
        }

        String employeeMobileNumber = employee.getMobileNumber();
        String employeeCreatedTimeStamp = employee.getCreatedTimeStamp();

        if (!mobileNumber.equals(employeeMobileNumber) || !timeStamp.equals(employeeCreatedTimeStamp)) {
            return new ValidateTokenResponseDto(false, "Invalid token", email); // Invalid token format
        }

        System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeee");
        LocalDateTime employeeDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(timeStamp, formatter);

        Duration duration = Duration.between(dateTime, employeeDateTime);
        long days = duration.toDays();
        if (days > 1) {
            return new ValidateTokenResponseDto(false, "Token expired", email); // Token expired
        }

        // Check if the email and mobile number match the expected values
        return new ValidateTokenResponseDto(true, "Valid token", email);
    }
}
