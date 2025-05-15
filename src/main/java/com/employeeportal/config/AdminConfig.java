package com.employeeportal.config;

import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;
import com.employeeportal.model.onboarding.Role;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.onboarding.EmployeeOrganizationDetailsRepository;
import com.employeeportal.repository.onboarding.RoleRepository;
import com.employeeportal.repository.registration.EmployeeRegRepository;
import com.employeeportal.repository.registration.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class AdminConfig {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRegRepository employeeRegRepository;
    private final EmployeeOrganizationDetailsRepository employeeOrganizationDetailsRepository;
    private final RoleRepository roleRepository;
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminConfig(AdminProperties adminProperties, PasswordEncoder passwordEncoder,
            EmployeeRepository employeeRepository, EmployeeRegRepository employeeRegRepository,
            RoleRepository roleRepository,
            EmployeeOrganizationDetailsRepository employeeOrganizationDetailsRepository) {

        this.employeeOrganizationDetailsRepository = employeeOrganizationDetailsRepository;
        this.adminProperties = adminProperties;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
        this.employeeRegRepository = employeeRegRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (!employeeRegRepository.existsByEmail(adminProperties.getEmailAddress())) {
            saveAdminDetails();
        }
        saveRoleDetails();
    }

    private void saveAdminDetails() {

        Role role = new Role();
        role.setRoleName("SUPER_ADMIN");
        roleRepository.save(role);

        EmployeeReg employeeReg = new EmployeeReg();
        employeeReg.setEmail(adminProperties.getEmailAddress());
        employeeReg.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
        employeeReg.setRole(role);

        Employee user = new Employee();
        user.setFirstName(adminProperties.getUserName());
        user.setEmail(adminProperties.getEmailAddress());
        user.setStatus("CREATED");

        user.setEmployeeReg(employeeReg);
        employeeReg.setEmployee(user);

        employeeRepository.save(user);

        EmployeeOrganizationDetails employeeOrganizationDetails = new EmployeeOrganizationDetails();
        employeeOrganizationDetails.setEmployee(user);
        employeeOrganizationDetails.setRole(role);
        employeeOrganizationDetailsRepository.save(employeeOrganizationDetails);

    }

    private void saveRoleDetails() {
        Role roleAdmin = roleRepository.findByRoleName("ADMIN");
        if (roleAdmin == null) {
            roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");
            roleRepository.save(roleAdmin);
        }

        Role roleEmployee = roleRepository.findByRoleName("EMPLOYEE");
        if (roleEmployee == null) {
            roleEmployee = new Role();
            roleEmployee.setRoleName("EMPLOYEE");
            roleRepository.save(roleEmployee);
        }
    }
}
