package com.employeeportal.model.onboarding;

import javax.persistence.*;

import com.employeeportal.exception.EncryptionException;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.util.EncryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "identification_details")
public class IdentificationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", nullable = false, length = 50)
    private IdentityType identityType;

    @Column(name = "identification_number", nullable = false, length = 100)
    private String identificationNumber;

    @Column(name = "identification_url", length = 255)
    private String identificationUrl;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setIdentificationNumber(String identificationNumber) {
        if (identificationNumber != null) {
            try {
                String encryptedIdentificationNumber = EncryptionUtil.encrypt(identificationNumber);
                this.identificationNumber = encryptedIdentificationNumber;
            } catch (Exception e) {
                if (this.identityType == IdentityType.PAN) {
                    throw new EncryptionException("Error encrypting Pan card number");
                } else {
                    throw new EncryptionException("Error encrypting Aadhaar card number");
                }
            }
        }

    }

    public String getIdentificationNumber() {
        if (this.identificationNumber == null) {
            return null;
        }
        try {
            return EncryptionUtil.decrypt(this.identificationNumber);
        } catch (Exception e) {
            if (this.identityType == IdentityType.PAN) {
                throw new EncryptionException("Error decrypting Pan card number");
            } else {
                throw new EncryptionException("Error decrypting Aadhaar card number");
            }
        }
    }
}