package com.employeeportal.exception;

import com.employeeportal.config.ApplicationConstant;
import com.employeeportal.model.ApiErrorResponse;
import com.employeeportal.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        ApiErrorResponse error = new ApiErrorResponse(
                accessDeniedException.getMessage() + ": " + ApplicationConstant.AUTHENTICATED_BUT_NOT_AUTHORIZED,
                HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(ResponseUtil.toJson(error));
    }
}
