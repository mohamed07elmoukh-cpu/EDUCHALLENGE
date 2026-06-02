package com.educhallenge.users.dto;

public class UserResponse {

	private Long id;
	private String username;
	private String email;
	private String role;
	private Integer pointsTotal;
	private Integer level;

	public UserResponse() {
	}

	public UserResponse(Long id, String username, String email, String role, Integer pointsTotal, Integer level) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
		this.pointsTotal = pointsTotal;
		this.level = level;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getPointsTotal() {
		return pointsTotal;
	}

	public void setPointsTotal(Integer pointsTotal) {
		this.pointsTotal = pointsTotal;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
