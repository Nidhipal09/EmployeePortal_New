package com.employeeportal.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateOtpDto {

     String email;
     boolean isOtpValid;

}
