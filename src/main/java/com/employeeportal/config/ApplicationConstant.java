package com.employeeportal.config;

public interface ApplicationConstant {

    String SUCCESS_RESPONSE_MSG = "Request Completed Successfully";
    String CREATE_ERROR_RESPONSE_MSG = "Record Not Created Successfully";
    String INVALID_TOKEN_MESSAGE = " : Please provide a valid token";
    String GET_ERROR_RESPONSE_MSG = "Record Not found";
    String AUTHENTICATED_BUT_NOT_AUTHORIZED = "User is not authorized to perform this action";
    String EDIT_ERROR_RESPONSE_MSG = "Record Not Updated Successfully";
    String EDIT_SUCCESS_RESPONSE_MSG = "Record Updated Successfully";
    String AUTHORIZATION_ERROR = "Authorization failed: No user with these details found";
    String AUTHORIZATION_EMAIL_ERROR = "Please enter a valid email address";
    String AUTHORIZATION_PASSWORD_ERROR = "Invalid password, please try again";
    String AUTHENTICATION_EMAIL_FAILED = "Invalid email: email with this address does not exist";
    String AUTHENTICATION_MOBILE_NUMBER_FAILED = "Invalid mobile number: user with this number does not exist";
    String HEADERS = "Authorization";
    String AUTH_TYPE = "Bearer ";
    String EXIST_EMAIL = "This email is already exists";
    String SWAGGER_PATH = "com.employeeportal.controller";
    String SWAGGER_AUTHORIZATION = "Authorization";

    String SWAGGER_HEADER = "header";

    String SWAGGER_GLOBAL = "global";

    String SWAGGER_ACCESS = "accessEverything";

    String LOGIN_RESPONSE = " Logged in successfully";
    String LOGOUT_RESPONSE = "Logged out successfully";

    String EMAIL_MESSAGE = "Email sent successfully.";
    String PASSWORD_RESET_EMAIL = "Password reset instructions sent to your email.";
    String EMAIL_ERROR_MESSAGE = "Email didn't sent.";
    String GET_SUCCESS_RESPONSE_MSG = "Record Found Successfully";
    String CREATED_SUCCESS_RESPONSE_MSG = "Record Created Successfully";
    String DELETE_SUCCESS_RESPONSE_MSG = "Record Deleted Successfully";
    String DELETE_FAILURE_RESPONSE_MSG = "Record Not Deleted Successfully";
    String EXISTS_ERROR_RESPONSE_MSG = "Record already exists";
}
