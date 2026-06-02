package com.educhallenge.challenges.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminChallengeStepUpdateRequest {

	@NotBlank(message = "Question text is required")
	@Size(max = 500, message = "Question text must not exceed 500 characters")
	private String questionText;

	@NotNull(message = "Step order is required")
	@Min(value = 1, message = "Step order must be at least 1")
	private Integer stepOrder;

	@NotNull(message = "Question points are required")
	@Min(value = 1, message = "Question points must be at least 1")
	@Max(value = 100, message = "Question points must not exceed 100")
	private Integer points;

	public AdminChallengeStepUpdateRequest() {
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}
}
