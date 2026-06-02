package com.educhallenge.backend.exception;

public class WeakPasswordException extends RuntimeException {

	public WeakPasswordException(String message) {
		super(message);
	}
}
