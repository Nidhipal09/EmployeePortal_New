package com.employeeportal.model.onboarding;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

}