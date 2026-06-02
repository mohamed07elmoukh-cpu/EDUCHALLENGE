package com.educhallenge.challenges.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

public class AdminChallengeDetailsResponse {

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
	private Integer stepCount;
	private AdminChallengeStatsResponse stats;
	private List<AdminChallengeStepResponse> steps;

	public AdminChallengeDetailsResponse() {
	}

	public AdminChallengeDetailsResponse(
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
			Integer stepCount,
			AdminChallengeStatsResponse stats,
			List<AdminChallengeStepResponse> steps
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
		this.stepCount = stepCount;
		this.stats = stats;
		this.steps = steps;
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

	public Integer getStepCount() {
		return stepCount;
	}

	public void setStepCount(Integer stepCount) {
		this.stepCount = stepCount;
	}

	public AdminChallengeStatsResponse getStats() {
		return stats;
	}

	public void setStats(AdminChallengeStatsResponse stats) {
		this.stats = stats;
	}

	public List<AdminChallengeStepResponse> getSteps() {
		return steps;
	}

	public void setSteps(List<AdminChallengeStepResponse> steps) {
		this.steps = steps;
	}
}
