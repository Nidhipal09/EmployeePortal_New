package com.employeeportal.dto.onboarding;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreviewDto {

    private String signatureUrl;
    private String place;

    @Pattern(regexp = "^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$", message = "Date must be in the format yyyy/MM/dd")
    private String date;
}
