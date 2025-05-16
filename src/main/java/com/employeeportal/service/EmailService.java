package com.employeeportal.service;

import com.employeeportal.config.EmailConstant;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.employeeportal.config.EmailProperties;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    private final EmailProperties emailProperties;

    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender emailSender, EmailProperties emailProperties, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.emailProperties = emailProperties;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(String email, String randomNumber, String subject, String templateName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getUserName());
            helper.setTo(email);

            Context ctx = new Context();
            ctx.setVariable("otpCode", randomNumber);
            ctx.setVariable("activateLink", EmailConstant.ACTIVE_SIGNUP_LINK + "?token=" + randomNumber);
            ctx.setVariable("resetLink", EmailConstant.RESET_PASSWORD_LINK + "?token=" + randomNumber);

            helper.setText(templateEngine.process(templateName, ctx), true);
            helper.setSubject(subject);
            emailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendEmail(String email, String randomNumber, String subject, String templateName, String other) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getUserName());
            helper.setTo(email);

            Context ctx = new Context();
            ctx.setVariable("otpCode", randomNumber);
            ctx.setVariable("activateLink", EmailConstant.ACTIVE_SIGNUP_LINK + "?token=" + randomNumber);
            ctx.setVariable("resetLink", EmailConstant.RESET_PASSWORD_LINK + "?token=" + randomNumber);
            ctx.setVariable("employeeEmail", other);

            helper.setText(templateEngine.process(templateName, ctx), true);
            helper.setSubject(subject);
            emailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendRegistrationEmail(String email, String password, String subject, String templateName, String token) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getUserName());
            helper.setTo(email);
            Context ctx = new Context();
            ctx.setVariable("email", email);
            ctx.setVariable("password", password);
            ctx.setVariable("token", token);
            helper.setText(templateEngine.process(templateName, ctx), true);
            helper.setSubject(subject);
            emailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendLeaveNotification(String toEmail,
            String employeeName,
            String leaveType,
            String fromDate,
            String toDate,
            int totalLeaveDays,
            String reason,
            String statusType,
            String subject,
            String templateName,
            String dayOfLeave) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getUserName());
            helper.setTo(toEmail);

            Context ctx = new Context();
            ctx.setVariable("employeeName", employeeName);
            ctx.setVariable("dayOfLeave", dayOfLeave);
            ctx.setVariable("leaveType", leaveType);
            ctx.setVariable("fromDate", fromDate);
            ctx.setVariable("toDate", toDate);
            ctx.setVariable("totalLeaveDays", totalLeaveDays);
            ctx.setVariable("reason", reason);
            ctx.setVariable("statusType", statusType);

            helper.setText(templateEngine.process(templateName, ctx), true);
            helper.setSubject(subject);
            emailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendPreviewEmailToHr(String email, String employeeSignatureUrl, String signatureDate, String place,
            String previewDetailsSubject, String previewDetailsTemplateName, String previewDetailsUrl) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getUserName());
            helper.setTo(email);

            Context ctx = new Context();
            ctx.setVariable("employeeSignatureUrl", employeeSignatureUrl);
            ctx.setVariable("signatureDate", signatureDate);
            ctx.setVariable("place", place);
            ctx.setVariable("previewDetailsUrl", previewDetailsUrl);

            helper.setText(templateEngine.process(previewDetailsTemplateName, ctx), true);
            helper.setSubject(previewDetailsSubject);
            emailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException(e.getMessage());
        }
    }

}
