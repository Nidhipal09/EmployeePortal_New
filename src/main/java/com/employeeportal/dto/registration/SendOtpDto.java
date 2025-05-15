package com.employeeportal.dto.registration;

public class SendOtpDto {

	private String email;

	public SendOtpDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SendOtpDto(String email) {
		super();
		this.email = email;
	}

}
