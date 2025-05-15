package com.employeeportal.model.registration;

import lombok.Data;

import javax.persistence.*;

import com.employeeportal.model.onboarding.AdditionalDetails;
import com.employeeportal.model.onboarding.Address;
import com.employeeportal.model.onboarding.Education;
import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;
import com.employeeportal.model.onboarding.EmploymentHistory;
import com.employeeportal.model.onboarding.IdentificationDetails;
import com.employeeportal.model.onboarding.PassportDetails;
import com.employeeportal.model.onboarding.PersonalDetails;
import com.employeeportal.model.onboarding.ProfessionalReferences;
import com.employeeportal.model.onboarding.Relatives;
import com.employeeportal.model.onboarding.VisaDetails;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String status;
    private String createdTimeStamp;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private PersonalDetails personalDetails;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Address> addressesDetails;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Education> educationDetails;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<IdentificationDetails> identificationDetails;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmployeeOrganizationDetails employeeOrganizationDetails;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private PassportDetails passportDetails;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private VisaDetails visaDetails;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ProfessionalReferences> professionalReferences;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Relatives> relatives;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private AdditionalDetails additionalDetails;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmploymentHistory> employmentHistories;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private EmployeeReg employeeReg;

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getDateOfBirth() {
        if (dateOfBirth != null) {
            return dateOfBirth.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        return null;
    }
}
