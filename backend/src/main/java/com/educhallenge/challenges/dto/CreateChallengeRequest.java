package com.educhallenge.challenges.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateChallengeRequest {

	@NotBlank(message = "Title is required")
	@Size(min = 5, max = 150, message = "Title must contain between 5 and 150 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(min = 20, max = 3000, message = "Description must contain between 20 and 3000 characters")
	private String description;

	@NotBlank(message = "Category is required")
	@Size(max = 80, message = "Category must not exceed 80 characters")
	private String category;

	@NotBlank(message = "Difficulty is required")
	private String difficulty;

	@NotNull(message = "Points reward is required")
	@Min(value = 10, message = "Points reward must be at least 10")
	@Max(value = 1000, message = "Points reward must not exceed 1000")
	private Integer pointsReward;

	@Size(max = 20, message = "Visibility must not exceed 20 characters")
	private String visibility;

	@NotEmpty(message = "Challenge must contain at least one question")
	private List<@Valid QuestionRequest> questions;

	public CreateChallengeRequest() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public Integer getPointsReward() {
		return pointsReward;
	}

	public void setPointsReward(Integer pointsReward) {
		this.pointsReward = pointsReward;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public List<QuestionRequest> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionRequest> questions) {
		this.questions = questions;
	}

	public static class QuestionRequest {

		@NotBlank(message = "Question text is required")
		@Size(max = 500, message = "Question text must not exceed 500 characters")
		private String questionText;

		@Min(value = 1, message = "Question points must be at least 1")
		@Max(value = 100, message = "Question points must not exceed 100")
		private Integer points;

		@NotEmpty(message = "Each question must have at least two options")
		@Size(min = 2, message = "Each question must have at least two options")
		private List<@Valid OptionRequest> options;

		public QuestionRequest() {
		}

		public String getQuestionText() {
			return questionText;
		}

		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}

		public Integer getPoints() {
			return points;
		}

		public void setPoints(Integer points) {
			this.points = points;
		}

		public List<OptionRequest> getOptions() {
			return options;
		}

		public void setOptions(List<OptionRequest> options) {
			this.options = options;
		}
	}

	public static class OptionRequest {

		@NotBlank(message = "Option text is required")
		@Size(max = 255, message = "Option text must not exceed 255 characters")
		private String optionText;

		@NotNull(message = "Option correctness is required")
		private Boolean isCorrect;

		public OptionRequest() {
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
}
