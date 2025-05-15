package com.employeeportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component

public class EmailProperties {

	@Value("${spring.mail.host}")
	private String host;
	@Value("${spring.mail.port}")
	private String port;
	@Value("${spring.mail.username}")
	private String userName;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String startTtlsEnable;
	@Value("${spring.mail.properties.mail.smtp.starttls.required}")
	private String startTtlsRequired;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;

	public EmailProperties(String host, String port, String userName, String password, String startTtlsEnable,
			String startTtlsRequired, String smtpAuth) {
		super();
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.startTtlsEnable = startTtlsEnable;
		this.startTtlsRequired = startTtlsRequired;
		this.smtpAuth = smtpAuth;
	}

	public EmailProperties() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStartTtlsEnable() {
		return startTtlsEnable;
	}

	public void setStartTtlsEnable(String startTtlsEnable) {
		this.startTtlsEnable = startTtlsEnable;
	}

	public String getStartTtlsRequired() {
		return startTtlsRequired;
	}

	public void setStartTtlsRequired(String startTtlsRequired) {
		this.startTtlsRequired = startTtlsRequired;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	@Override
	public String toString() {
		return "EmailProperties [host=" + host + ", port=" + port + ", userName=" + userName + ", password=" + password
				+ ", startTtlsEnable=" + startTtlsEnable + ", startTtlsRequired=" + startTtlsRequired + ", smtpAuth="
				+ smtpAuth + "]";
	}

}
