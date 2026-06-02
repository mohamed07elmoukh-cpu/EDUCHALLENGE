package com.educhallenge.gamification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "badges",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_badges_name", columnNames = "name"),
				@UniqueConstraint(name = "uq_badges_slug", columnNames = "slug")
		}
)
public class Badge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 80)
	private String slug;

	@Column(length = 255)
	private String description;

	@Column(name = "badge_type", length = 50)
	private String badgeType;

	@Column(name = "condition_type", nullable = false, length = 50)
	private String conditionType;

	@Column(name = "condition_value", nullable = false)
	private Integer conditionValue;

	@Column(length = 30)
	private String tone;

	@Column(name = "icon_name", length = 60)
	private String iconName;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public Badge() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(String badgeType) {
		this.badgeType = badgeType;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public Integer getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(Integer conditionValue) {
		this.conditionValue = conditionValue;
	}

	public String getTone() {
		return tone;
	}

	public void setTone(String tone) {
		this.tone = tone;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	public void applyDefaults() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (tone == null || tone.isBlank()) {
			tone = "bronze";
		}
	}
}
