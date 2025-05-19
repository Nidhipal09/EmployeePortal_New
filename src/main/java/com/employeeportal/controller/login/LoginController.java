package com.employeeportal.controller.login;

import com.employeeportal.config.ApplicationConstant;
import com.employeeportal.model.*;
import com.employeeportal.model.login.ResponseDTO;
import com.employeeportal.service.login.LoginService;
import com.employeeportal.util.ResponseUtil;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/public")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    @Autowired
    private ResponseUtil responseUtil;

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> verifyLogin(@RequestBody @Valid LoginRequest authRequest) {
        try {
            LoginResponse loginResponse = loginService.verifyLogin(authRequest);

            ResponseDTO responseDTO = new ResponseDTO(loginResponse.getRoleName() + ApplicationConstant.LOGIN_RESPONSE, HttpStatus.OK.value(), loginResponse, true);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (BadCredentialsException ex) {
            ResponseDTO response = responseUtil.prepareResponseDto(
                    null, ex.getMessage(),
                    HttpStatus.UNAUTHORIZED.value(), false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

        } catch (UsernameNotFoundException ex) {
            ResponseDTO response = responseUtil.prepareResponseDto(
                    null, ex.getMessage(),
                    HttpStatus.NOT_FOUND.value(), false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            ResponseDTO response = responseUtil.prepareResponseDto(
                    null, ApplicationConstant.AUTHORIZATION_ERROR,
                    HttpStatus.UNAUTHORIZED.value(), false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            loginService.sendPasswordResetEmail(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            loginService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {

        try {
            loginService.logout(token.substring(7));
            ResponseDTO response = responseUtil.prepareResponseDto(null,
                    ApplicationConstant.LOGOUT_RESPONSE,
                    HttpStatus.OK.value(), true);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResponseDTO response = responseUtil.prepareResponseDto(null,
                    ApplicationConstant.AUTHORIZATION_ERROR,
                    HttpStatus.UNAUTHORIZED.value(), false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
