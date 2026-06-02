package com.educhallenge.challenges.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SubmitChallengeAttemptRequest {

	@NotEmpty(message = "Please answer all questions before submitting")
	private List<@Valid AnswerRequest> answers;

	public SubmitChallengeAttemptRequest() {
	}

	public List<AnswerRequest> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerRequest> answers) {
		this.answers = answers;
	}

	public static class AnswerRequest {

		@NotNull(message = "Question identifier is required")
		private Long stepId;

		@NotNull(message = "Selected option identifier is required")
		private Long selectedOptionId;

		public AnswerRequest() {
		}

		public Long getStepId() {
			return stepId;
		}

		public void setStepId(Long stepId) {
			this.stepId = stepId;
		}

		public Long getSelectedOptionId() {
			return selectedOptionId;
		}

		public void setSelectedOptionId(Long selectedOptionId) {
			this.selectedOptionId = selectedOptionId;
		}
	}
}
