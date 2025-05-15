package com.employeeportal.serviceImpl.login;

import com.employeeportal.exception.EmployeeNotFoundException;
import com.employeeportal.model.registration.EmployeeReg;
import com.employeeportal.repository.registration.EmployeeRegRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRegRepository employeeRegRepository;

    @Autowired
    public UserDetailsServiceImpl(EmployeeRegRepository employeeRegRepository) {
        this.employeeRegRepository = employeeRegRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        EmployeeReg employeeReg = employeeRegRepository.findByEmail(principal.trim());
        if (employeeReg == null) {
            employeeReg = employeeRegRepository.findByMobileNumberFromEmployee(principal);

        }
        if (employeeReg != null) {
            System.out
                    .println("lllllllllllllllllllllllllll" + employeeReg.getEmail() + "  " + employeeReg.getPassword());
            return new User(employeeReg.getEmail(), employeeReg.getPassword(), employeeReg.getAuthorities());
        } else {
            throw new EmployeeNotFoundException();

        }
    }
}
