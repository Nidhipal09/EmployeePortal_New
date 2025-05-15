package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "professional_references")
public class ProfessionalReferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ProfessionalReferenceId;
    private String name;
    private String designation;
    private String email;
    private String contactNumber;
    private String relationship;
    private String companyName;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

}
