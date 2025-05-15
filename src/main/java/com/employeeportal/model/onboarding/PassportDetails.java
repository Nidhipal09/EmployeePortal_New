package com.employeeportal.model.onboarding;

import com.employeeportal.exception.EncryptionException;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.util.EncryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "passport_details")
public class PassportDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passportId;
    private String passportNumber;
    private LocalDate dateOfIssue;
    private String placeOfIssue;
    private String countryOfIssue;
    private LocalDate validUpto;
    private String nationality;
    private String passportUrl;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue == null ? null
                : LocalDate.parse(dateOfIssue, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getDateOfIssue() {
        return (dateOfIssue != null) ? dateOfIssue.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : null;
    }

    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto == null ? null
                : LocalDate.parse(validUpto, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getValidUpto() {
        return (validUpto != null) ? validUpto.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : null;
    }

    public void setPassportNumber(String passportNumber) {
        if (passportNumber != null) {
            try {
                String encryptedPassportNumber = EncryptionUtil.encrypt(passportNumber);
                this.passportNumber = encryptedPassportNumber;
            } catch (Exception e) {
                throw new EncryptionException("Error encrypting Passport number");
            }
        }

    }

    public String getPassportNumber() {
        if (this.passportNumber == null) {
            return null;
        }
        try {
            return EncryptionUtil.decrypt(this.passportNumber);
        } catch (Exception e) {
            throw new EncryptionException("Error decrypting Passport number");
        }
    }
}
