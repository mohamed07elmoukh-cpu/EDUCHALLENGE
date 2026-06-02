package com.educhallenge.challenges.entity;

import com.educhallenge.users.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
public class Challenge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String title;

	@Column(nullable = false, length = 3000)
	private String description;

	@Column(nullable = false, length = 80)
	private String category;

	@Column(nullable = false, length = 20)
	private String difficulty;

	@Column(name = "points_reward", nullable = false)
	private Integer pointsReward;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id")
	private User creator;

	@Column(name = "creator_username", nullable = false, length = 50)
	private String creatorUsername;

	@Column(nullable = false, length = 20)
	private String visibility;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("stepOrder ASC")
	private List<ChallengeStep> steps = new ArrayList<>();

	public Challenge() {
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

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
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

	public List<ChallengeStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ChallengeStep> steps) {
		this.steps.clear();

		if (steps == null) {
			return;
		}

		for (ChallengeStep step : steps) {
			addStep(step);
		}
	}

	public void addStep(ChallengeStep step) {
		step.setChallenge(this);
		this.steps.add(step);
	}

	public void clearSteps() {
		for (ChallengeStep step : steps) {
			step.setChallenge(null);
		}
		steps.clear();
	}

	@PrePersist
	public void applyDefaults() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (visibility == null || visibility.isBlank()) {
			visibility = "PUBLIC";
		}
		if (isActive == null) {
			isActive = true;
		}
		if (creator != null && (creatorUsername == null || creatorUsername.isBlank())) {
			creatorUsername = creator.getUsername();
		}
	}
}
