package com.employeeportal.exception;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.employeeportal.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(
			ResourceNotFoundException ex, WebRequest request) {
		String error = "Resource not found";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), error, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(EncryptionException.class)
	public ResponseEntity<Object> handleEncryptionException(
			EncryptionException ex, WebRequest request) {
		String error = "Error encrypting sensitive data";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), error, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<Object> handleDateParseException(
			DateTimeParseException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"Invalid date: Date should be of format dd/MM/yyyy", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(
			BadRequestException ex, WebRequest request) {
		String error = "Bad request";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), error, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<Object> handleAlreadyExistsException(
			AlreadyExistsException ex, WebRequest request) {
		String error = "Email already registered";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), error, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MobileNumberAlreadyExistsException.class)
	public ResponseEntity<Object> handleMobileNumberAlreadyExistsException(
			MobileNumberAlreadyExistsException ex, WebRequest request) {
		String error = "Mobile number already exists";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), error, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(FieldsMissingException.class)
	public ResponseEntity<String> handleFieldsMissingException(FieldsMissingException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errors = new LinkedHashMap<>();
		errors.put("status", HttpStatus.BAD_REQUEST.value());
		errors.put("error", "Validation Failed");

		List<Map<String, String>> validationErrors = new ArrayList<>();

		// Use a Set to store unique field-error pairs
		Set<String> seenFields = new HashSet<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			String field = error.getField();
			String message = error.getDefaultMessage();

			// Avoid adding duplicate errors
			if (!seenFields.contains(field + message)) {
				Map<String, String> errorDetail = new HashMap<>();
				errorDetail.put("field", field);
				errorDetail.put("message", message);
				validationErrors.add(errorDetail);
				seenFields.add(field + message);
			}
		}

		errors.put("details", validationErrors);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}

}
