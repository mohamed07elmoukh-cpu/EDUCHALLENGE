package com.educhallenge.challenges.dto;

import java.time.LocalDateTime;

public class ChallengeCommentResponse {

	private Long id;
	private Long authorId;
	private String authorUsername;
	private String authorEmail;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ChallengeCommentResponse() {
	}

	public ChallengeCommentResponse(
			Long id,
			Long authorId,
			String authorUsername,
			String authorEmail,
			String content,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {
		this.id = id;
		this.authorId = authorId;
		this.authorUsername = authorUsername;
		this.authorEmail = authorEmail;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
