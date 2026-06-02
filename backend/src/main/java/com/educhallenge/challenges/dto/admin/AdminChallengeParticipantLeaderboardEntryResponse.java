package com.educhallenge.challenges.dto.admin;

import java.time.LocalDateTime;

public class AdminChallengeParticipantLeaderboardEntryResponse {

	private Integer rank;
	private Long participantId;
	private String participantUsername;
	private String participantEmail;
	private Integer bestScore;
	private Long completedAttempts;
	private Long fastestCompletionSeconds;
	private LocalDateTime lastCompletedAt;

	public AdminChallengeParticipantLeaderboardEntryResponse() {
	}

	public AdminChallengeParticipantLeaderboardEntryResponse(
			Integer rank,
			Long participantId,
			String participantUsername,
			String participantEmail,
			Integer bestScore,
			Long completedAttempts,
			Long fastestCompletionSeconds,
			LocalDateTime lastCompletedAt
	) {
		this.rank = rank;
		this.participantId = participantId;
		this.participantUsername = participantUsername;
		this.participantEmail = participantEmail;
		this.bestScore = bestScore;
		this.completedAttempts = completedAttempts;
		this.fastestCompletionSeconds = fastestCompletionSeconds;
		this.lastCompletedAt = lastCompletedAt;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
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

	public Integer getBestScore() {
		return bestScore;
	}

	public void setBestScore(Integer bestScore) {
		this.bestScore = bestScore;
	}

	public Long getCompletedAttempts() {
		return completedAttempts;
	}

	public void setCompletedAttempts(Long completedAttempts) {
		this.completedAttempts = completedAttempts;
	}

	public Long getFastestCompletionSeconds() {
		return fastestCompletionSeconds;
	}

	public void setFastestCompletionSeconds(Long fastestCompletionSeconds) {
		this.fastestCompletionSeconds = fastestCompletionSeconds;
	}

	public LocalDateTime getLastCompletedAt() {
		return lastCompletedAt;
	}

	public void setLastCompletedAt(LocalDateTime lastCompletedAt) {
		this.lastCompletedAt = lastCompletedAt;
	}
}
