package com.educhallenge.gamification.dto;

import java.time.LocalDate;
import java.util.List;

public class GamificationMeResponse {

	private Long userId;
	private String username;
	private Integer totalPoints;
	private Integer level;
	private Integer nextLevelTarget;
	private Integer progressCurrent;
	private Integer progressMax;
	private Integer progressPercentage;
	private Integer pointsToNextLevel;
	private Integer completedChallenges;
	private Integer earnedBadgesCount;
	private Integer currentStreak;
	private LocalDate lastActivityDate;
	private Integer currentRank;
	private String topBadge;
	private List<BadgeResponse> earnedBadges;
	private List<BadgeResponse> lockedBadges;
	private List<NotificationResponse> recentAchievements;
	private List<RecentChallengeActivityResponse> recentActivity;
	private List<LeaderboardEntryResponse> miniLeaderboard;

	public GamificationMeResponse() {
	}

	public GamificationMeResponse(
			Long userId,
			String username,
			Integer totalPoints,
			Integer level,
			Integer nextLevelTarget,
			Integer progressCurrent,
			Integer progressMax,
			Integer progressPercentage,
			Integer pointsToNextLevel,
			Integer completedChallenges,
			Integer earnedBadgesCount,
			Integer currentStreak,
			LocalDate lastActivityDate,
			Integer currentRank,
			String topBadge,
			List<BadgeResponse> earnedBadges,
			List<BadgeResponse> lockedBadges,
			List<NotificationResponse> recentAchievements,
			List<RecentChallengeActivityResponse> recentActivity,
			List<LeaderboardEntryResponse> miniLeaderboard
	) {
		this.userId = userId;
		this.username = username;
		this.totalPoints = totalPoints;
		this.level = level;
		this.nextLevelTarget = nextLevelTarget;
		this.progressCurrent = progressCurrent;
		this.progressMax = progressMax;
		this.progressPercentage = progressPercentage;
		this.pointsToNextLevel = pointsToNextLevel;
		this.completedChallenges = completedChallenges;
		this.earnedBadgesCount = earnedBadgesCount;
		this.currentStreak = currentStreak;
		this.lastActivityDate = lastActivityDate;
		this.currentRank = currentRank;
		this.topBadge = topBadge;
		this.earnedBadges = earnedBadges;
		this.lockedBadges = lockedBadges;
		this.recentAchievements = recentAchievements;
		this.recentActivity = recentActivity;
		this.miniLeaderboard = miniLeaderboard;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getNextLevelTarget() {
		return nextLevelTarget;
	}

	public void setNextLevelTarget(Integer nextLevelTarget) {
		this.nextLevelTarget = nextLevelTarget;
	}

	public Integer getProgressCurrent() {
		return progressCurrent;
	}

	public void setProgressCurrent(Integer progressCurrent) {
		this.progressCurrent = progressCurrent;
	}

	public Integer getProgressMax() {
		return progressMax;
	}

	public void setProgressMax(Integer progressMax) {
		this.progressMax = progressMax;
	}

	public Integer getProgressPercentage() {
		return progressPercentage;
	}

	public void setProgressPercentage(Integer progressPercentage) {
		this.progressPercentage = progressPercentage;
	}

	public Integer getPointsToNextLevel() {
		return pointsToNextLevel;
	}

	public void setPointsToNextLevel(Integer pointsToNextLevel) {
		this.pointsToNextLevel = pointsToNextLevel;
	}

	public Integer getCompletedChallenges() {
		return completedChallenges;
	}

	public void setCompletedChallenges(Integer completedChallenges) {
		this.completedChallenges = completedChallenges;
	}

	public Integer getEarnedBadgesCount() {
		return earnedBadgesCount;
	}

	public void setEarnedBadgesCount(Integer earnedBadgesCount) {
		this.earnedBadgesCount = earnedBadgesCount;
	}

	public Integer getCurrentStreak() {
		return currentStreak;
	}

	public void setCurrentStreak(Integer currentStreak) {
		this.currentStreak = currentStreak;
	}

	public LocalDate getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(LocalDate lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public Integer getCurrentRank() {
		return currentRank;
	}

	public void setCurrentRank(Integer currentRank) {
		this.currentRank = currentRank;
	}

	public String getTopBadge() {
		return topBadge;
	}

	public void setTopBadge(String topBadge) {
		this.topBadge = topBadge;
	}

	public List<BadgeResponse> getEarnedBadges() {
		return earnedBadges;
	}

	public void setEarnedBadges(List<BadgeResponse> earnedBadges) {
		this.earnedBadges = earnedBadges;
	}

	public List<BadgeResponse> getLockedBadges() {
		return lockedBadges;
	}

	public void setLockedBadges(List<BadgeResponse> lockedBadges) {
		this.lockedBadges = lockedBadges;
	}

	public List<NotificationResponse> getRecentAchievements() {
		return recentAchievements;
	}

	public void setRecentAchievements(List<NotificationResponse> recentAchievements) {
		this.recentAchievements = recentAchievements;
	}

	public List<RecentChallengeActivityResponse> getRecentActivity() {
		return recentActivity;
	}

	public void setRecentActivity(List<RecentChallengeActivityResponse> recentActivity) {
		this.recentActivity = recentActivity;
	}

	public List<LeaderboardEntryResponse> getMiniLeaderboard() {
		return miniLeaderboard;
	}

	public void setMiniLeaderboard(List<LeaderboardEntryResponse> miniLeaderboard) {
		this.miniLeaderboard = miniLeaderboard;
	}
}
