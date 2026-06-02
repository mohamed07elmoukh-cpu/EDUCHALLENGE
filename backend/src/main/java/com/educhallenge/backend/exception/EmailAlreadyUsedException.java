package com.educhallenge.backend.exception;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException(String message) {
		super(message);
	}
}
