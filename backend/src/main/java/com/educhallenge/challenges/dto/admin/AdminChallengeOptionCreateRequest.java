package com.educhallenge.challenges.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminChallengeOptionCreateRequest {

	@NotBlank(message = "Option text is required")
	@Size(max = 255, message = "Option text must not exceed 255 characters")
	private String optionText;

	@NotNull(message = "Option correctness is required")
	private Boolean isCorrect;

	public AdminChallengeOptionCreateRequest() {
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
