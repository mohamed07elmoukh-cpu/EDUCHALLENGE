package com.educhallenge.backend.exception;

public class UsernameAlreadyUsedException extends RuntimeException {

	public UsernameAlreadyUsedException(String message) {
		super(message);
	}
}
