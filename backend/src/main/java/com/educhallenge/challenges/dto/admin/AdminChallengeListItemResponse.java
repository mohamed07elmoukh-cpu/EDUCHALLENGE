package com.educhallenge.challenges.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminChallengeListItemResponse {

	private Long id;
	private String title;
	private String category;
	private String difficulty;
	private Integer pointsReward;
	private Long creatorId;
	private String creatorUsername;
	private String visibility;
	private Boolean isActive;
	private LocalDateTime createdAt;
	private Long stepCount;
	private Long totalAttempts;
	private Long uniqueParticipants;
	private BigDecimal averageScore;
	private Long completionCount;

	public AdminChallengeListItemResponse() {
	}

	public AdminChallengeListItemResponse(
			Long id,
			String title,
			String category,
			String difficulty,
			Integer pointsReward,
			Long creatorId,
			String creatorUsername,
			String visibility,
			Boolean isActive,
			LocalDateTime createdAt,
			Long stepCount,
			Long totalAttempts,
			Long uniqueParticipants,
			BigDecimal averageScore,
			Long completionCount
	) {
		this.id = id;
		this.title = title;
		this.category = category;
		this.difficulty = difficulty;
		this.pointsReward = pointsReward;
		this.creatorId = creatorId;
		this.creatorUsername = creatorUsername;
		this.visibility = visibility;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.stepCount = stepCount;
		this.totalAttempts = totalAttempts;
		this.uniqueParticipants = uniqueParticipants;
		this.averageScore = averageScore;
		this.completionCount = completionCount;
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

	public Long getStepCount() {
		return stepCount;
	}

	public void setStepCount(Long stepCount) {
		this.stepCount = stepCount;
	}

	public Long getTotalAttempts() {
		return totalAttempts;
	}

	public void setTotalAttempts(Long totalAttempts) {
		this.totalAttempts = totalAttempts;
	}

	public Long getUniqueParticipants() {
		return uniqueParticipants;
	}

	public void setUniqueParticipants(Long uniqueParticipants) {
		this.uniqueParticipants = uniqueParticipants;
	}

	public BigDecimal getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(BigDecimal averageScore) {
		this.averageScore = averageScore;
	}

	public Long getCompletionCount() {
		return completionCount;
	}

	public void setCompletionCount(Long completionCount) {
		this.completionCount = completionCount;
	}
}
