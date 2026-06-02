package com.educhallenge.gamification.dto;

import java.time.LocalDateTime;

public class BadgeResponse {

	private Long id;
	private String slug;
	private String title;
	private String description;
	private String badgeType;
	private String tone;
	private String iconName;
	private String conditionType;
	private Integer conditionValue;
	private Boolean earned;
	private LocalDateTime earnedAt;

	public BadgeResponse() {
	}

	public BadgeResponse(
			Long id,
			String slug,
			String title,
			String description,
			String badgeType,
			String tone,
			String iconName,
			String conditionType,
			Integer conditionValue,
			Boolean earned,
			LocalDateTime earnedAt
	) {
		this.id = id;
		this.slug = slug;
		this.title = title;
		this.description = description;
		this.badgeType = badgeType;
		this.tone = tone;
		this.iconName = iconName;
		this.conditionType = conditionType;
		this.conditionValue = conditionValue;
		this.earned = earned;
		this.earnedAt = earnedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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

	public String getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(String badgeType) {
		this.badgeType = badgeType;
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

	public Boolean getEarned() {
		return earned;
	}

	public void setEarned(Boolean earned) {
		this.earned = earned;
	}

	public LocalDateTime getEarnedAt() {
		return earnedAt;
	}

	public void setEarnedAt(LocalDateTime earnedAt) {
		this.earnedAt = earnedAt;
	}
}
