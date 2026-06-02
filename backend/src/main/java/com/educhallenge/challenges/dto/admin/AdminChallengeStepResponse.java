package com.educhallenge.challenges.dto.admin;

import java.util.List;

public class AdminChallengeStepResponse {

	private Long id;
	private Long challengeId;
	private String questionText;
	private Integer stepOrder;
	private Integer points;
	private Integer optionCount;
	private List<AdminChallengeOptionResponse> options;

	public AdminChallengeStepResponse() {
	}

	public AdminChallengeStepResponse(
			Long id,
			Long challengeId,
			String questionText,
			Integer stepOrder,
			Integer points,
			Integer optionCount,
			List<AdminChallengeOptionResponse> options
	) {
		this.id = id;
		this.challengeId = challengeId;
		this.questionText = questionText;
		this.stepOrder = stepOrder;
		this.points = points;
		this.optionCount = optionCount;
		this.options = options;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(Long challengeId) {
		this.challengeId = challengeId;
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

	public Integer getOptionCount() {
		return optionCount;
	}

	public void setOptionCount(Integer optionCount) {
		this.optionCount = optionCount;
	}

	public List<AdminChallengeOptionResponse> getOptions() {
		return options;
	}

	public void setOptions(List<AdminChallengeOptionResponse> options) {
		this.options = options;
	}
}
