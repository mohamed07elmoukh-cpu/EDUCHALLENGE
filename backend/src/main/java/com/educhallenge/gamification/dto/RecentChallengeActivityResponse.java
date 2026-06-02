package com.educhallenge.gamification.dto;

import java.time.LocalDateTime;

public class RecentChallengeActivityResponse {

	private Long attemptId;
	private Long challengeId;
	private String challengeTitle;
	private Integer score;
	private Integer maxScore;
	private LocalDateTime completedAt;
	private String outcomeLabel;

	public RecentChallengeActivityResponse() {
	}

	public RecentChallengeActivityResponse(
			Long attemptId,
			Long challengeId,
			String challengeTitle,
			Integer score,
			Integer maxScore,
			LocalDateTime completedAt,
			String outcomeLabel
	) {
		this.attemptId = attemptId;
		this.challengeId = challengeId;
		this.challengeTitle = challengeTitle;
		this.score = score;
		this.maxScore = maxScore;
		this.completedAt = completedAt;
		this.outcomeLabel = outcomeLabel;
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public String getOutcomeLabel() {
		return outcomeLabel;
	}

	public void setOutcomeLabel(String outcomeLabel) {
		this.outcomeLabel = outcomeLabel;
	}
}
