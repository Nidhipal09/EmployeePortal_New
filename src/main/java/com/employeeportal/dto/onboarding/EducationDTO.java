package com.employeeportal.dto.onboarding;

import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class EducationDTO {
    private String degreeName;
    private String institutionName;
    private String location;
    private String fieldOfStudy;
    private String rollNumber;

    @Pattern(regexp = "^([A-F]\\+?|\\d{1,2}(\\.\\d{1,2})?\\s?cgpa?%)$", message = "Grade or percentage must be valid)")
    private String gradeOrCGPA;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String fromDate;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String toDate;
    // private String passingCertificateUrl;
    private String degreeCertificateUrl;
}