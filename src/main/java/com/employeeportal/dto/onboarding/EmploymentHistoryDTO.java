package com.employeeportal.dto.onboarding;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class EmploymentHistoryDTO {
    private String previousEmployerName;
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Telephone number must be exactly 10 digits")
    private String mobileNumber;
    private String employeeCode;
    private String designation;
    private String department;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String managerName;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must be a valid email address")
    private String managerEmail;

    @Pattern(regexp = "\\d{10}", message = "Contact number must be exactly 10 digits")
    private String managerContactNo;
    private String reasonForLeaving;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String employmentPeriodFrom;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String employmentPeriodTo;

    private String positionType;
    private String experienceCertificateUrl;
    private String relievingLetterUrl;

    // private String lastMonthSalarySlipUrl;
    private String lastMonthSalarySlip1Url;
    private String lastMonthSalarySlip2Url;
    private String lastMonthSalarySlip3Url;

    private String appointmentLetterUrl;
}