package com.employeeportal.serviceImpl.registration;

import com.employeeportal.dto.registration.UserDto;
import com.employeeportal.exception.BadRequestException;
import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;
import com.employeeportal.model.onboarding.Role;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.onboarding.EmployeeOrganizationDetailsRepository;
import com.employeeportal.repository.onboarding.RoleRepository;
import com.employeeportal.repository.registration.EmployeeRegRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.registration.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public UserDto saveUsers(UserDto user) {

        Employee alreadyExistsEmployee = employeeRepository.findByEmail(user.getEmail());
        if (alreadyExistsEmployee != null) {
            throw new BadRequestException("User with this email is already registered.");
        }

        Employee employee = null;
        if (user.getRoleName().equals("EMPLOYEE")) {
            employee = employeeRepository.findByMobileNumber(user.getMobileNumber()).get();
            employee.setFirstName(user.getFirstName());
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

            EmployeeReg employeeReg = employeeRegRepository.findByEmployeeId(employee.getEmployeeId());
            employeeReg.setEmail(user.getEmail());
            employeeReg.setPassword(user.getPassword());
            employeeReg.setRole(role);
            employeeReg.setEmployee(employee);

            employeeRepository.save(employee);

        } else {

            employee = new Employee();
            employee.setFirstName(user.getFirstName());
            employee.setLastName(user.getLastName());
            employee.setEmail(user.getEmail());
            employee.setMobileNumber(user.getMobileNumber());

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

            EmployeeReg employeeReg = new EmployeeReg();
            employeeReg.setEmail(user.getEmail());
            employeeReg.setPassword(user.getPassword());
            employeeReg.setRole(role);
            employeeReg.setEmployee(employee);

            employeeRepository.save(employee);
        }


        emailService.sendRegistrationEmail(user.getEmail(), user.getPassword(), "Users Registration Successfully",
                "registration.html");

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
        if (employeeRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("User already registered.");
        }

        Optional<Employee> employeeBox = employeeRepository.findById(usersId.intValue());
        if (employeeBox.isPresent()) {
            Employee employee = employeeBox.get();
            int employeeId = employee.getEmployeeId();

            EmployeeOrganizationDetails employeeOrganizationDetails = employeeOrganizationDetailsRepository
                    .findByEmployeeId(employeeId).get();
            EmployeeReg employeeReg = employeeRegRepository.findByEmployeeId(employeeId);

            if (user.getPassword() != null)
                employeeReg.setPassword(user.getPassword());

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

        }

        emailService.sendRegistrationEmail(user.getEmail(), user.getPassword(), "Users Registration Successfully",
                "registration.html");
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

        return user;
    }

}
