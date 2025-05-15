package com.employeeportal.dto.onboarding;

import lombok.Data;

import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class VisaDetailsDTO {
    private String workPermitDetails;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String workPermitValidTill;
    private String passportCopy;
    private String passportCopyUrl;
    private String status;
    private String country;

    @JsonIgnore
    public boolean isNull() {
        return this == null ||
                (this.workPermitDetails == null &&
                        this.workPermitValidTill == null &&
                        this.passportCopy == null &&
                        this.passportCopyUrl == null &&
                        this.status == null &&
                        this.country == null);
    }
}