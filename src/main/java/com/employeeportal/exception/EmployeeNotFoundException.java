package com.employeeportal.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException() {
        super("we are not able to find employee details in our system");
    }
}
