package com.educhallenge.challenges.dto.admin;

public class AdminChallengeOptionResponse {

	private Long id;
	private Long stepId;
	private String optionText;
	private Boolean isCorrect;

	public AdminChallengeOptionResponse() {
	}

	public AdminChallengeOptionResponse(Long id, Long stepId, String optionText, Boolean isCorrect) {
		this.id = id;
		this.stepId = stepId;
		this.optionText = optionText;
		this.isCorrect = isCorrect;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean correct) {
		isCorrect = correct;
	}
}
