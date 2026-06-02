package com.educhallenge.challenges.dto;

import java.time.LocalDateTime;

public class ChallengeResponse {

	private Long id;
	private String title;
	private String description;
	private String category;
	private String difficulty;
	private Integer pointsReward;
	private LocalDateTime createdAt;
	private String creatorUsername;
	private String visibility;
	private Boolean isActive;

	public ChallengeResponse() {
	}

	public ChallengeResponse(
			Long id,
			String title,
			String description,
			String category,
			String difficulty,
			Integer pointsReward,
			LocalDateTime createdAt,
			String creatorUsername,
			String visibility,
			Boolean isActive
	) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.difficulty = difficulty;
		this.pointsReward = pointsReward;
		this.createdAt = createdAt;
		this.creatorUsername = creatorUsername;
		this.visibility = visibility;
		this.isActive = isActive;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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
}
