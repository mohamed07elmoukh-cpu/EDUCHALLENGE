package com.educhallenge.backend.exception;

public class BusinessRuleViolationException extends RuntimeException {

	public BusinessRuleViolationException(String message) {
		super(message);
	}
}
