package com.employeeportal.service.login;

import com.employeeportal.model.LoginRequest;
import com.employeeportal.model.LoginResponse;

public interface LoginService {

	LoginResponse verifyLogin(LoginRequest loginRequest) throws Exception;

	void sendPasswordResetEmail(String email) throws Exception;

	void resetPassword(String token, String newPassword) throws Exception;

	void logout(String token);
}