package com.educhallenge.challenges.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChallengeDetailsResponse {

	private Long id;
	private String title;
	private String description;
	private String category;
	private String difficulty;
	private Integer pointsReward;
	private Long creatorId;
	private String creatorUsername;
	private String visibility;
	private Boolean isActive;
	private LocalDateTime createdAt;
	private ChallengeSocialSummaryResponse social;
	private List<ChallengeCommentResponse> comments;
	private List<QuestionResponse> questions;

	public ChallengeDetailsResponse() {
	}

	public ChallengeDetailsResponse(
			Long id,
			String title,
			String description,
			String category,
			String difficulty,
			Integer pointsReward,
			Long creatorId,
			String creatorUsername,
			String visibility,
			Boolean isActive,
			LocalDateTime createdAt,
			ChallengeSocialSummaryResponse social,
			List<ChallengeCommentResponse> comments,
			List<QuestionResponse> questions
	) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.difficulty = difficulty;
		this.pointsReward = pointsReward;
		this.creatorId = creatorId;
		this.creatorUsername = creatorUsername;
		this.visibility = visibility;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.social = social;
		this.comments = comments;
		this.questions = questions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorUsername() {
		return creatorUsername;
	}

	public void setCreatorUsername(String creatorUsername) {
		this.creatorUsername = creatorUsername;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean active) {
		isActive = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<QuestionResponse> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResponse> questions) {
		this.questions = questions;
	}

	public ChallengeSocialSummaryResponse getSocial() {
		return social;
	}

	public void setSocial(ChallengeSocialSummaryResponse social) {
		this.social = social;
	}

	public List<ChallengeCommentResponse> getComments() {
		return comments;
	}

	public void setComments(List<ChallengeCommentResponse> comments) {
		this.comments = comments;
	}

	public static class QuestionResponse {

		private Long id;
		private String questionText;
		private Integer stepOrder;
		private Integer points;
		private List<OptionResponse> options;

		public QuestionResponse() {
		}

		public QuestionResponse(
				Long id,
				String questionText,
				Integer stepOrder,
				Integer points,
				List<OptionResponse> options
		) {
			this.id = id;
			this.questionText = questionText;
			this.stepOrder = stepOrder;
			this.points = points;
			this.options = options;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
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

		public List<OptionResponse> getOptions() {
			return options;
		}

		public void setOptions(List<OptionResponse> options) {
			this.options = options;
		}
	}

	public static class OptionResponse {

		private Long id;
		private String optionText;
		private Boolean isCorrect;

		public OptionResponse() {
		}

		public OptionResponse(Long id, String optionText, Boolean isCorrect) {
			this.id = id;
			this.optionText = optionText;
			this.isCorrect = isCorrect;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
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
