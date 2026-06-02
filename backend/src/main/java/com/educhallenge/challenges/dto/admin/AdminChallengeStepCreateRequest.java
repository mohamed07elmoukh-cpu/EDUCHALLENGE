package com.educhallenge.challenges.dto.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class AdminChallengeStepCreateRequest {

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

	@NotEmpty(message = "Each step must have at least two options")
	@Size(min = 2, message = "Each step must have at least two options")
	private List<@Valid AdminChallengeOptionCreateRequest> options;

	public AdminChallengeStepCreateRequest() {
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

	public List<AdminChallengeOptionCreateRequest> getOptions() {
		return options;
	}

	public void setOptions(List<AdminChallengeOptionCreateRequest> options) {
		this.options = options;
	}
}
