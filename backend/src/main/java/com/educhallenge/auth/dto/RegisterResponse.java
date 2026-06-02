package com.educhallenge.auth.dto;

import com.educhallenge.users.dto.UserResponse;

public class RegisterResponse {

	private String message;
	private UserResponse user;

	public RegisterResponse() {
	}

	public RegisterResponse(String message, UserResponse user) {
		this.message = message;
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UserResponse getUser() {
		return user;
	}

	public void setUser(UserResponse user) {
		this.user = user;
	}
}
