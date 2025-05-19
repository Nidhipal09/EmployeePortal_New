package com.employeeportal.serviceImpl.login;
import java.util.*;
import com.employeeportal.model.*;
import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;
import com.employeeportal.model.onboarding.Role;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.*;
import com.employeeportal.repository.onboarding.EmployeeOrganizationDetailsRepository;
import com.employeeportal.repository.onboarding.RoleRepository;
import com.employeeportal.repository.registration.EmployeeRegRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.employeeportal.config.ApplicationConstant;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.login.LoginService;
import com.employeeportal.serviceImpl.logout.TokenBlacklistService;

@Service
public class LoginServiceImpl implements LoginService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    private final JwtRepository jwtRepository;
    private final JwtUtil jwtUtil;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRegRepository employeeRegRepository;

    @Autowired
    private EmployeeOrganizationDetailsRepository employeeOrganizationDetailsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    public LoginServiceImpl(EmployeeRepository employeeRepository, JwtRepository jwtRepository, JwtUtil jwtUtil,
            EmailService emailService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.jwtRepository = jwtRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse verifyLogin(LoginRequest loginRequest) throws Exception {

        String email = loginRequest.getEmail();
        String mobileNumber = loginRequest.getMobileNumber();

        if (email.equals("") && mobileNumber.equals("")) {
            throw new BadCredentialsException("Kindly enter email or mobile number to login");
        }

        if (!email.equals("") && !mobileNumber.equals("")) {
            throw new BadCredentialsException("Kindly use either email or mobile number to login");
        }

        EmployeeReg employeeReg = employeeRegRepository.findByEmail(loginRequest.getEmail());
        // Find the employee by email using EmployeeRegRepository

        if (employeeReg == null) {
            employeeReg = employeeRegRepository.findByMobileNumberFromEmployee(mobileNumber);
            if (employeeReg == null) {
                employeeReg = employeeRegRepository.findBySecondaryMobileNumberFromEmployee(mobileNumber);
                if (employeeReg == null) {
                    if (email.equals(""))
                        throw new BadCredentialsException(ApplicationConstant.AUTHENTICATION_MOBILE_NUMBER_FAILED);
                    else
                        throw new BadCredentialsException(ApplicationConstant.AUTHENTICATION_EMAIL_FAILED);

                }
            }
        }
        System.out.println(employeeReg.getPassword() + " " + loginRequest.getPassword());
        // Check if the email matches and validate the password

        if (!passwordEncoder.matches(loginRequest.getPassword(), employeeReg.getPassword())) {
            throw new BadCredentialsException(ApplicationConstant.AUTHORIZATION_PASSWORD_ERROR);
        }

        // Prepare LoginResponse
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(employeeReg.getEmployee().getEmail());

        // Fetch full name from Employee table
        Employee employee = employeeRepository.findByEmployeeId(employeeReg.getEmployee().getEmployeeId());
        String firstName = employee.getFirstName() == null ? "" : employee.getFirstName() + " ";
        String middleName = employee.getMiddleName() == null ? "" : employee.getMiddleName() + " ";
        String lastName = employee.getLastName() == null ? "" : employee.getLastName();
        String fullName = firstName + middleName + lastName;
        fullName = fullName.trim();
        loginResponse.setFullName(fullName);

        // Fetch role name from Role table using role ID from
        // EmployeeOrganizationDetails
        EmployeeOrganizationDetails orgDetails = employeeOrganizationDetailsRepository
                .findByEmployeeId(employeeReg.getEmployee().getEmployeeId()).get();
        Role role = roleRepository.findById(orgDetails.getRole().getRoleId()).get();
        loginResponse.setRoleName(role.getRoleName());

        // Generate JWT token
        loginResponse.setToken(jwtUtil.generateToken(employeeReg.getEmail()));

        return loginResponse;
    }

    @Override
    public void sendPasswordResetEmail(String email) throws Exception {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new Exception("employee not found");
        }

        String token = UUID.randomUUID().toString();
        jwtRepository.save(new JwtEntity(token, true, employee.getEmployeeId()));

        emailService.sendEmail(email, token, "Password Reset Request", "passwordResetTemplate");
    }

    @Override
    public void resetPassword(String token, String newPassword) throws Exception {

        JwtEntity jwtEntity = jwtRepository.findByJtiAndValidSession(token, true);
        if (jwtEntity == null) {
            throw new Exception("Invalid token or employee not found");
        }
        jwtRepository.deleteById(jwtEntity.getId());

        EmployeeReg employeeReg = employeeRegRepository.findByEmployeeId(jwtEntity.getPrimaryId());
        employeeReg.setPassword(passwordEncoder.encode(newPassword));
        employeeRegRepository.save(employeeReg);
        System.out.println("Password updated successfully for employee: " + employeeReg.getEmail());
    }

    public void logout(String token) {

        tokenBlacklistService.blacklistToken(token);
        Claims claims = jwtUtil.getClaims(token);
        JwtEntity jwtEntity = jwtRepository.findByJtiAndValidSession(claims.getId(), true);
        if (jwtEntity != null) {
            jwtEntity.setValidSession(false);
            jwtRepository.saveAndFlush(jwtEntity);
        }
    }

}
