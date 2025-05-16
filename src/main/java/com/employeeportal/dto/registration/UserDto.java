package com.employeeportal.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int employeeId;
    private String employeeCode;
    private String designation;
    private String reportingManager;
    private String reportingHr;
    private String projects;
    private String joiningDate;
    private String roleName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNumber;

}
