package com.employeeportal.model.onboarding;

import javax.persistence.*;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_organization_details")
public class EmployeeOrganizationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeOrganizationId;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "reporting_manager", length = 100)
    private String reportingManager;

    @Column(name = "reporting_hr", length = 100)
    private String reportingHr;

    private String projects;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public void setJoiningDate(String joiningDate) {

        this.joiningDate = joiningDate == null ? null
                : LocalDate.parse(joiningDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // Custom getter returning String
    public String getJoiningDate() {
        return (joiningDate != null) ? joiningDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : null;
    }
}