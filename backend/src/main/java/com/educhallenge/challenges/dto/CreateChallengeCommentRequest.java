package com.educhallenge.challenges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateChallengeCommentRequest {

	@NotBlank(message = "Comment content is required.")
	@Size(max = 800, message = "Comment must contain at most 800 characters.")
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
