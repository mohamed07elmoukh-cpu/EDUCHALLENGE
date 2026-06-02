package com.educhallenge.challenges.dto;

import java.time.LocalDateTime;

public class MyChallengeResponse {

	private Long id;
	private String title;
	private String description;
	private String category;
	private String difficulty;
	private Integer pointsReward;
	private Long questionsCount;
	private Long participantsCount;
	private Long attemptsCount;
	private String status;
	private LocalDateTime createdAt;

	public MyChallengeResponse() {
	}

	public MyChallengeResponse(
			Long id,
			String title,
			String description,
			String category,
			String difficulty,
			Integer pointsReward,
			Long questionsCount,
			Long participantsCount,
			Long attemptsCount,
			String status,
			LocalDateTime createdAt
	) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.difficulty = difficulty;
		this.pointsReward = pointsReward;
		this.questionsCount = questionsCount;
		this.participantsCount = participantsCount;
		this.attemptsCount = attemptsCount;
		this.status = status;
		this.createdAt = createdAt;
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

	public Long getQuestionsCount() {
		return questionsCount;
	}

	public void setQuestionsCount(Long questionsCount) {
		this.questionsCount = questionsCount;
	}

	public Long getParticipantsCount() {
		return participantsCount;
	}

	public void setParticipantsCount(Long participantsCount) {
		this.participantsCount = participantsCount;
	}

	public Long getAttemptsCount() {
		return attemptsCount;
	}

	public void setAttemptsCount(Long attemptsCount) {
		this.attemptsCount = attemptsCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
