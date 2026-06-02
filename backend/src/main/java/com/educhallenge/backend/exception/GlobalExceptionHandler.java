package com.educhallenge.backend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();

		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return buildResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				fieldErrors
		);
	}

	@ExceptionHandler({
			EmailAlreadyUsedException.class,
			UsernameAlreadyUsedException.class
	})
	public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException exception) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), null);
	}

	@ExceptionHandler(WeakPasswordException.class)
	public ResponseEntity<ApiErrorResponse> handleWeakPassword(WeakPasswordException exception) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException exception) {
		return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), null);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
	}

	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException exception) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
		String message = "Resource already exists";
		String errorText = exception.getMostSpecificCause() != null
				? exception.getMostSpecificCause().getMessage()
				: exception.getMessage();

		if (errorText != null && errorText.contains("uk_users_email")) {
			message = "Email already used";
		} else if (errorText != null && errorText.contains("uk_users_username")) {
			message = "Username already used";
		} else if (errorText != null && errorText.contains("uq_challenge_steps_challenge_order")) {
			message = "Step order must be unique within the challenge";
		} else if (errorText != null && errorText.contains("uq_step_options_step_text")) {
			message = "Option text already exists for this step";
		}

		return buildResponse(HttpStatus.CONFLICT, message, null);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException exception) {
		HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
		return buildResponse(status, exception.getReason(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(
			HttpStatus status,
			String message,
			Map<String, String> fieldErrors
	) {
		ApiErrorResponse response = new ApiErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				fieldErrors
		);

		return ResponseEntity.status(status).body(response);
	}
}
