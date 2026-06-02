package com.educhallenge.challenges.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminChallengeUpdateRequest {

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

	private Boolean isActive;

	public AdminChallengeUpdateRequest() {
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean active) {
		isActive = active;
	}
}
