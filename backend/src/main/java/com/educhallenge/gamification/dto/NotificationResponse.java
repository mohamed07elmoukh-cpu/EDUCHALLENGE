package com.educhallenge.gamification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

	private Long id;
	private String title;
	private String message;
	private String notificationType;
	private Boolean isRead;
	private LocalDateTime createdAt;

	public NotificationResponse() {
	}

	public NotificationResponse(
			Long id,
			String title,
			String message,
			String notificationType,
			Boolean isRead,
			LocalDateTime createdAt
	) {
		this.id = id;
		this.title = title;
		this.message = message;
		this.notificationType = notificationType;
		this.isRead = isRead;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean read) {
		isRead = read;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
