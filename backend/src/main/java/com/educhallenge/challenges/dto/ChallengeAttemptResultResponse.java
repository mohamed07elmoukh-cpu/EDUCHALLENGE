package com.educhallenge.challenges.dto;

import com.educhallenge.gamification.dto.BadgeResponse;
import com.educhallenge.gamification.dto.NotificationResponse;
import java.time.LocalDateTime;
import java.util.List;

public class ChallengeAttemptResultResponse {

	private Long attemptId;
	private Long challengeId;
	private String challengeTitle;
	private String status;
	private Boolean firstCompletion;
	private Integer totalQuestions;
	private Integer correctAnswers;
	private Integer earnedScore;
	private Integer maxScore;
	private Integer awardedPoints;
	private Integer totalPoints;
	private Integer level;
	private Integer currentStreak;
	private Integer currentRank;
	private LocalDateTime completedAt;
	private List<BadgeResponse> unlockedBadges;
	private List<NotificationResponse> notifications;
	private List<AnswerResultResponse> answers;

	public ChallengeAttemptResultResponse() {
	}

	public ChallengeAttemptResultResponse(
			Long attemptId,
			Long challengeId,
			String challengeTitle,
			String status,
			Boolean firstCompletion,
			Integer totalQuestions,
			Integer correctAnswers,
			Integer earnedScore,
			Integer maxScore,
			Integer awardedPoints,
			Integer totalPoints,
			Integer level,
			Integer currentStreak,
			Integer currentRank,
			LocalDateTime completedAt,
			List<BadgeResponse> unlockedBadges,
			List<NotificationResponse> notifications,
			List<AnswerResultResponse> answers
	) {
		this.attemptId = attemptId;
		this.challengeId = challengeId;
		this.challengeTitle = challengeTitle;
		this.status = status;
		this.firstCompletion = firstCompletion;
		this.totalQuestions = totalQuestions;
		this.correctAnswers = correctAnswers;
		this.earnedScore = earnedScore;
		this.maxScore = maxScore;
		this.awardedPoints = awardedPoints;
		this.totalPoints = totalPoints;
		this.level = level;
		this.currentStreak = currentStreak;
		this.currentRank = currentRank;
		this.completedAt = completedAt;
		this.unlockedBadges = unlockedBadges;
		this.notifications = notifications;
		this.answers = answers;
	}

	public Long getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(Long attemptId) {
		this.attemptId = attemptId;
	}

	public Long getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(Long challengeId) {
		this.challengeId = challengeId;
	}

	public String getChallengeTitle() {
		return challengeTitle;
	}

	public void setChallengeTitle(String challengeTitle) {
		this.challengeTitle = challengeTitle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getFirstCompletion() {
		return firstCompletion;
	}

	public void setFirstCompletion(Boolean firstCompletion) {
		this.firstCompletion = firstCompletion;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getEarnedScore() {
		return earnedScore;
	}

	public void setEarnedScore(Integer earnedScore) {
		this.earnedScore = earnedScore;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public Integer getAwardedPoints() {
		return awardedPoints;
	}

	public void setAwardedPoints(Integer awardedPoints) {
		this.awardedPoints = awardedPoints;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getCurrentStreak() {
		return currentStreak;
	}

	public void setCurrentStreak(Integer currentStreak) {
		this.currentStreak = currentStreak;
	}

	public Integer getCurrentRank() {
		return currentRank;
	}

	public void setCurrentRank(Integer currentRank) {
		this.currentRank = currentRank;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public List<BadgeResponse> getUnlockedBadges() {
		return unlockedBadges;
	}

	public void setUnlockedBadges(List<BadgeResponse> unlockedBadges) {
		this.unlockedBadges = unlockedBadges;
	}

	public List<NotificationResponse> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationResponse> notifications) {
		this.notifications = notifications;
	}

	public List<AnswerResultResponse> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerResultResponse> answers) {
		this.answers = answers;
	}

	public static class AnswerResultResponse {

		private Long stepId;
		private Integer stepOrder;
		private String questionText;
		private Long selectedOptionId;
		private Long correctOptionId;
		private Boolean isCorrect;
		private Integer points;

		public AnswerResultResponse() {
		}

		public AnswerResultResponse(
				Long stepId,
				Integer stepOrder,
				String questionText,
				Long selectedOptionId,
				Long correctOptionId,
				Boolean isCorrect,
				Integer points
		) {
			this.stepId = stepId;
			this.stepOrder = stepOrder;
			this.questionText = questionText;
			this.selectedOptionId = selectedOptionId;
			this.correctOptionId = correctOptionId;
			this.isCorrect = isCorrect;
			this.points = points;
		}

		public Long getStepId() {
			return stepId;
		}

		public void setStepId(Long stepId) {
			this.stepId = stepId;
		}

		public Integer getStepOrder() {
			return stepOrder;
		}

		public void setStepOrder(Integer stepOrder) {
			this.stepOrder = stepOrder;
		}

		public String getQuestionText() {
			return questionText;
		}

		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}

		public Long getSelectedOptionId() {
			return selectedOptionId;
		}

		public void setSelectedOptionId(Long selectedOptionId) {
			this.selectedOptionId = selectedOptionId;
		}

		public Long getCorrectOptionId() {
			return correctOptionId;
		}

		public void setCorrectOptionId(Long correctOptionId) {
			this.correctOptionId = correctOptionId;
		}

		public Boolean getIsCorrect() {
			return isCorrect;
		}

		public void setIsCorrect(Boolean correct) {
			isCorrect = correct;
		}

		public Integer getPoints() {
			return points;
		}

		public void setPoints(Integer points) {
			this.points = points;
		}
	}
}
