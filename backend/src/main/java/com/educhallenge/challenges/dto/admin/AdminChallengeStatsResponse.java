package com.educhallenge.challenges.dto.admin;

import java.math.BigDecimal;

public class AdminChallengeStatsResponse {

	private Long challengeId;
	private Long totalAttempts;
	private Long uniqueParticipants;
	private BigDecimal averageScore;
	private Long completionCount;

	public AdminChallengeStatsResponse() {
	}

	public AdminChallengeStatsResponse(
			Long challengeId,
			Long totalAttempts,
			Long uniqueParticipants,
			BigDecimal averageScore,
			Long completionCount
	) {
		this.challengeId = challengeId;
		this.totalAttempts = totalAttempts;
		this.uniqueParticipants = uniqueParticipants;
		this.averageScore = averageScore;
		this.completionCount = completionCount;
	}

	public Long getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(Long challengeId) {
		this.challengeId = challengeId;
	}

	public Long getTotalAttempts() {
		return totalAttempts;
	}

	public void setTotalAttempts(Long totalAttempts) {
		this.totalAttempts = totalAttempts;
	}

	public Long getUniqueParticipants() {
		return uniqueParticipants;
	}

	public void setUniqueParticipants(Long uniqueParticipants) {
		this.uniqueParticipants = uniqueParticipants;
	}

	public BigDecimal getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(BigDecimal averageScore) {
		this.averageScore = averageScore;
	}

	public Long getCompletionCount() {
		return completionCount;
	}

	public void setCompletionCount(Long completionCount) {
		this.completionCount = completionCount;
	}
}
