package com.educhallenge.auth.dto;

import com.educhallenge.users.dto.UserResponse;

public class LoginResponse {

	private String message;
	private UserResponse user;
	private String accessToken;
	private String tokenType;
	private long expiresIn;

	public LoginResponse() {
	}

	public LoginResponse(String message, UserResponse user, String accessToken, String tokenType, long expiresIn) {
		this.message = message;
		this.user = user;
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
