package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "employment_history")
public class EmploymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employmentId;
    private String previousEmployerName;
    private String address;
    private String mobileNumber;
    private String employeeCode;
    private String designation;
    private String department;
    private String managerName;
    private String managerEmail;
    private String managerContactNo;
    private String reasonForLeaving;
    private LocalDate employmentPeriodFrom;
    private LocalDate employmentPeriodTo;
    private String positionType;
    private String experienceCertificateUrl;
    private String relievingLetterUrl;
    // private String lastMonthSalarySlipUrl;
    private String lastMonthSalarySlip1Url;
    private String lastMonthSalarySlip2Url;
    private String lastMonthSalarySlip3Url;

    private String appointmentLetterUrl;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setEmploymentPeriodFrom(String employmentPeriodFrom) {
        this.employmentPeriodFrom = employmentPeriodFrom == null ? null
                : LocalDate.parse(employmentPeriodFrom, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getEmploymentPeriodFrom() {
        return (employmentPeriodFrom != null) ? employmentPeriodFrom.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                : null;
    }

    public void setEmploymentPeriodTo(String employmentPeriodTo) {
        this.employmentPeriodTo = employmentPeriodTo == null ? null
                : LocalDate.parse(employmentPeriodTo, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getEmploymentPeriodTo() {
        return (employmentPeriodTo != null) ? employmentPeriodTo.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                : null;
    }
}
