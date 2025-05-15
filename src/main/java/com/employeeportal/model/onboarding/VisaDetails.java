package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "visa_details")
public class VisaDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visaDetailsId;
    private String workPermitDetails;
    private LocalDate workPermitValidTill;
    private String passportCopy;
    private String passportCopyUrl;
    private String status;
    private String country;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setWorkPermitValidTill(String workPermitValidTill) {
        this.workPermitValidTill = workPermitValidTill == null ? null
                : LocalDate.parse(workPermitValidTill, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getWorkPermitValidTill() {
        return (workPermitValidTill != null) ? workPermitValidTill.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                : null;
    }
}
