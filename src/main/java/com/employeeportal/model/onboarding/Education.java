package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "education")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int educationId;
    private String degreeName;
    private String institutionName;
    private String location;
    private String fieldOfStudy;
    private String rollNumber;
    private String gradeOrCGPA;
    private LocalDate fromDate;
    private LocalDate toDate;
    // private String passingCertificateUrl;
    private String degreeCertificateUrl;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate == null ? null : LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public void setToDate(String toDate) {
        this.toDate = toDate == null ? null : LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getFromDate() {
        return fromDate == null ? null : fromDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getToDate() {
        return toDate == null ? null : toDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
