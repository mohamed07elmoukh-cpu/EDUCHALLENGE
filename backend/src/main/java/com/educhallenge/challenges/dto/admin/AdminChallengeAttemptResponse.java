package com.educhallenge.challenges.dto.admin;

import java.time.LocalDateTime;

public class AdminChallengeAttemptResponse {

	private Long attemptId;
	private Long challengeId;
	private Long participantId;
	private String participantUsername;
	private String participantEmail;
	private String status;
	private Integer score;
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;
	private Long durationSeconds;

	public AdminChallengeAttemptResponse() {
	}

	public AdminChallengeAttemptResponse(
			Long attemptId,
			Long challengeId,
			Long participantId,
			String participantUsername,
			String participantEmail,
			String status,
			Integer score,
			LocalDateTime startedAt,
			LocalDateTime completedAt,
			Long durationSeconds
	) {
		this.attemptId = attemptId;
		this.challengeId = challengeId;
		this.participantId = participantId;
		this.participantUsername = participantUsername;
		this.participantEmail = participantEmail;
		this.status = status;
		this.score = score;
		this.startedAt = startedAt;
		this.completedAt = completedAt;
		this.durationSeconds = durationSeconds;
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

	public Long getParticipantId() {
		return participantId;
	}

	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}

	public String getParticipantUsername() {
		return participantUsername;
	}

	public void setParticipantUsername(String participantUsername) {
		this.participantUsername = participantUsername;
	}

	public String getParticipantEmail() {
		return participantEmail;
	}

	public void setParticipantEmail(String participantEmail) {
		this.participantEmail = participantEmail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public Long getDurationSeconds() {
		return durationSeconds;
	}

	public void setDurationSeconds(Long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
}
