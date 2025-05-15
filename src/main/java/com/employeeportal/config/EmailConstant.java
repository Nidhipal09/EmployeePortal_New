package com.employeeportal.config;

public interface EmailConstant {

    String SIGN_UP_LINK_TEMPLATE_NAME = "create-signUp-link";

    String SIGN_UP_LINK_SUBJECT = "Send SignUp Activate-Link";
    String RESEND_LINK_SUBJECT = "Resend Activate-Link";
    String SIGN_UP_OTP_TEMPLATE_NAME = "otp-SignUp";

    String SIGN_UP_OTP_SUBJECT = "Send SignUp Otp";

    String ACTIVE_SIGNUP_LINK = "http://localhost:3000/enterOtp";
    // New link for password reset
    String RESET_PASSWORD_LINK = "http://localhost:3000/resetPassword";

    String LEAVE_NOTIFICATION_SUBJECT = "Request For Leave Approval -";
    String LEAVE_NOTIFICATION_TEMPLATE_NAME = "leave-notification.html";
    String PREVIEW_DETAILS_SUBJECT = "kindly check my preview details";
    String PREVIEW_DETAILS_TEMPLATE_NAME = "preview-details";
    String PREVIEW_DETAILS_LINK = "http://localhost:3000/primary/preview/";

    String VERIFY_EMPLOYEE_DETAILS_SUBJECT = "Verify employee details";
    String VERIFY_EMPLOYEE_DETAILS_TEMPLATE = "Verify-employee-details.html";
}