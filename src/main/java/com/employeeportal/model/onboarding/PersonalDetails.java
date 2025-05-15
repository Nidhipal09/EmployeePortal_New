package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "personal_details")
public class PersonalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personalDetailsId;
    private String imageUrl;
    private String personalEmail;
    private String gender;
    private String motherName;
    private String fatherName;
    private String secondaryMobile;

    @OneToOne
    @JoinColumn(name = "employeeId")
    @JsonIgnore
    private Employee employee;

}
