package com.employeeportal.serviceImpl.registration;

import com.employeeportal.dto.registration.UserDto;
import com.employeeportal.exception.BadRequestException;
import com.employeeportal.model.JwtEntity;
import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;
import com.employeeportal.model.onboarding.Role;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.JwtRepository;
import com.employeeportal.repository.onboarding.EmployeeOrganizationDetailsRepository;
import com.employeeportal.repository.onboarding.RoleRepository;
import com.employeeportal.repository.registration.EmployeeRegRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.registration.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeeRegRepository employeeRegRepository;

    @Autowired
    private EmployeeOrganizationDetailsRepository employeeOrganizationDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtRepository jwtRepository;

    @Override
    public UserDto saveUsers(UserDto user) {

        Employee alreadyExistsEmployee = employeeRepository.findByEmail(user.getEmail());
        if (alreadyExistsEmployee != null) {
            throw new BadRequestException("User with this email is already registered.");
        }

        Employee employee = null;
        if (user.getRoleName().equals("EMPLOYEE")) {
            employee = employeeRepository.findByMobileNumber(user.getMobileNumber()).get();
            if (employee != null) {
                employee.setFirstName(user.getFirstName());
                if (!user.getLastName().equals(""))
                    employee.setLastName(user.getLastName());
                employee.setEmail(user.getEmail());

                EmployeeOrganizationDetails employeeOrganizationDetails = new EmployeeOrganizationDetails();
                employeeOrganizationDetails.setEmployeeCode(user.getEmployeeCode());
                employeeOrganizationDetails.setDesignation(user.getDesignation());
                employeeOrganizationDetails.setReportingManager(user.getReportingManager());
                employeeOrganizationDetails.setReportingHr(user.getReportingHr());
                employeeOrganizationDetails.setProjects(user.getProjects());
                employeeOrganizationDetails.setJoiningDate(user.getJoiningDate());

                Role role = roleRepository.findByRoleName(user.getRoleName());
                employeeOrganizationDetails.setRole(role);
                employeeOrganizationDetails.setEmployee(employee);
                employee.setEmployeeOrganizationDetails(employeeOrganizationDetails);

                EmployeeReg employeeReg = employeeRegRepository.findByEmployeeId(employee.getEmployeeId());
                employeeReg.setEmail(user.getEmail());
                employeeReg.setPassword(passwordEncoder.encode(user.getPassword()));
                employeeReg.setRole(role);
                employeeReg.setEmployee(employee);

                employeeRepository.save(employee);
            }

        } else {
            // for admin or super admin
            employee = new Employee();
            employee.setFirstName(user.getFirstName());
            employee.setLastName(user.getLastName());
            employee.setEmail(user.getEmail());
            employee.setMobileNumber(user.getMobileNumber());
            employee.setStatus("CREATED");

            EmployeeOrganizationDetails employeeOrganizationDetails = new EmployeeOrganizationDetails();
            employeeOrganizationDetails.setEmployeeCode(user.getEmployeeCode());
            employeeOrganizationDetails.setDesignation(user.getDesignation());
            employeeOrganizationDetails.setReportingManager(user.getReportingManager());
            employeeOrganizationDetails.setReportingHr(user.getReportingHr());
            employeeOrganizationDetails.setProjects(user.getProjects());
            employeeOrganizationDetails.setJoiningDate(user.getJoiningDate());

            Role role = roleRepository.findByRoleName(user.getRoleName());
            employeeOrganizationDetails.setRole(role);
            employeeOrganizationDetails.setEmployee(employee);
            employee.setEmployeeOrganizationDetails(employeeOrganizationDetails);

            EmployeeReg employeeReg = new EmployeeReg();
            employeeReg.setEmail(user.getEmail());
            employeeReg.setPassword(passwordEncoder.encode(user.getPassword()));
            employeeReg.setRole(role);
            employeeReg.setEmployee(employee);
            employee.setEmployeeReg(employeeReg);

            employeeRepository.save(employee);
        }

        String token = UUID.randomUUID().toString();
        jwtRepository.save(new JwtEntity(token, true, employee.getEmployeeId()));

        emailService.sendRegistrationEmail(user.getEmail(), user.getPassword(), "User Registered Successfully",
                "registration.html", token);

        Employee registeredUser = employeeRepository.findByEmail(user.getEmail());
        user.setEmployeeId(registeredUser.getEmployeeId());
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<Employee> employees = employeeRepository.findAll();
        List<UserDto> users = new ArrayList<>();

        employees.forEach(employee -> {
            UserDto user = employeeToUser(employee);
            users.add(user);
        });

        return users;

    }

    @Override
    public UserDto updateUsersById(Long usersId, UserDto user) {
        Optional<Employee> employeeBox = employeeRepository.findById(usersId.intValue());
        if (!employeeBox.isPresent()) {
            throw new BadRequestException("User doesn't exists.");
        }

        Employee employee = employeeBox.get();
        int employeeId = employee.getEmployeeId();

        EmployeeOrganizationDetails employeeOrganizationDetails = employeeOrganizationDetailsRepository
                .findByEmployeeId(employeeId).get();
        EmployeeReg employeeReg = employeeRegRepository.findByEmployeeId(employeeId);

        if (user.getPassword() != null)
            employeeReg.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getFirstName() != null)
            employee.setFirstName(user.getFirstName());
        if (user.getLastName() != null)
            employee.setLastName(user.getLastName());
        if (user.getMobileNumber() != null)
            employee.setMobileNumber(user.getMobileNumber());
        if (user.getEmail() != null) {
            employee.setEmail(user.getEmail());
            employeeReg.setEmail(user.getEmail());
        }

        if (user.getEmployeeCode() != null)
            employeeOrganizationDetails.setEmployeeCode(user.getEmployeeCode());
        if (user.getDesignation() != null)
            employeeOrganizationDetails.setDesignation(user.getDesignation());
        if (user.getReportingManager() != null)
            employeeOrganizationDetails.setReportingManager(user.getReportingManager());
        if (user.getReportingHr() != null)
            employeeOrganizationDetails.setReportingHr(user.getReportingHr());
        if (user.getProjects() != null)
            employeeOrganizationDetails.setProjects(user.getProjects());
        if (user.getJoiningDate() != null)
            employeeOrganizationDetails.setJoiningDate(user.getJoiningDate());

        if (user.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(user.getRoleName());
            employeeOrganizationDetails.setRole(role);
            employeeReg.setRole(role);
        }

        employeeRepository.save(employee);

        emailService.sendRegistrationEmail(user.getEmail(), user.getPassword(), "User updated Successfully",
                "update-user.html", null);

        user.setEmployeeId(employeeId);        
        return user;
    }

    private UserDto employeeToUser(Employee employee) {
        EmployeeOrganizationDetails employeeOrganizationDetails = employee.getEmployeeOrganizationDetails();
        EmployeeReg employeeReg = employee.getEmployeeReg();

        UserDto user = new UserDto();
        user.setEmployeeCode(employeeOrganizationDetails.getEmployeeCode());
        user.setDesignation(employeeOrganizationDetails.getDesignation());
        user.setReportingManager(employeeOrganizationDetails.getReportingManager());
        user.setReportingHr(employeeOrganizationDetails.getReportingHr());
        user.setProjects(employeeOrganizationDetails.getProjects());
        user.setJoiningDate(employeeOrganizationDetails.getJoiningDate());
        user.setRoleName(employeeOrganizationDetails.getRole().getRoleName());

        user.setEmail(employeeReg.getEmail());
        user.setPassword(employeeReg.getPassword());

        user.setFirstName(employee.getFirstName());
        user.setLastName(employee.getLastName());
        user.setMobileNumber(employee.getMobileNumber());

        user.setEmployeeId(employee.getEmployeeId());

        return user;
    }

}
