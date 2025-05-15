package com.employeeportal.model.onboarding;

import lombok.Data;
import javax.persistence.*;

import com.employeeportal.model.registration.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;
    private String addressType;
    private String address1;
    private String address2;
    private String address3;
    private String zip;
    private String state;
    private String city;
    private LocalDate stayFrom;
    private LocalDate stayTo;
    private String emergencyContactNumber;
    private String emergencyContactNameAndRelationship;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setStayFrom(String stayFrom) {
        this.stayFrom = stayFrom == null ? null : LocalDate.parse(stayFrom, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public void setStayTo(String stayTo) {
        this.stayTo = stayTo == null ? null : LocalDate.parse(stayTo, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getStayFrom() {
        return stayFrom == null ? null : stayFrom.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getStayTo() {
        return stayTo == null ? null : stayTo.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}