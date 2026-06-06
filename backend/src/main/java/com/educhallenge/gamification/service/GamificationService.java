package com.educhallenge.gamification.service;

import com.educhallenge.challenges.entity.Challenge;
import com.educhallenge.challenges.entity.ChallengeAttempt;
import com.educhallenge.challenges.entity.ChallengeStep;
import com.educhallenge.challenges.repository.ChallengeAttemptRepository;
import com.educhallenge.gamification.dto.BadgeResponse;
import com.educhallenge.gamification.dto.GamificationMeResponse;
import com.educhallenge.gamification.dto.LeaderboardEntryResponse;
import com.educhallenge.gamification.dto.LeaderboardResponse;
import com.educhallenge.gamification.dto.NotificationResponse;
import com.educhallenge.gamification.dto.RecentChallengeActivityResponse;
import com.educhallenge.gamification.entity.Badge;
import com.educhallenge.gamification.entity.Notification;
import com.educhallenge.gamification.entity.UserBadge;
import com.educhallenge.gamification.repository.BadgeRepository;
import com.educhallenge.gamification.repository.NotificationRepository;
import com.educhallenge.gamification.repository.UserBadgeRepository;
import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GamificationService {

	private static final int LEVEL_STEP_POINTS = 100;

	private final UserRepository userRepository;
	private final ChallengeAttemptRepository challengeAttemptRepository;
	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final NotificationRepository notificationRepository;

	public GamificationService(
			UserRepository userRepository,
			ChallengeAttemptRepository challengeAttemptRepository,
			BadgeRepository badgeRepository,
			UserBadgeRepository userBadgeRepository,
			NotificationRepository notificationRepository
	) {
		this.userRepository = userRepository;
		this.challengeAttemptRepository = challengeAttemptRepository;
		this.badgeRepository = badgeRepository;
		this.userBadgeRepository = userBadgeRepository;
		this.notificationRepository = notificationRepository;
	}

	public ChallengeGamificationUpdate applyChallengeCompletion(
			User user,
			Challenge challenge,
			ChallengeAttempt attempt,
			int totalQuestions,
			int correctAnswers,
			int earnedScore,
			int maxScore,
			boolean firstCompletion
	) {
		int previousLevel = safeLevel(user.getLevel());
		int awardedPoints = firstCompletion ? calculateAwardedPoints(earnedScore, maxScore, challenge.getPointsReward()) : 0;
		int updatedTotalPoints = applyAwardedPoints(user, awardedPoints);
		int updatedLevel = safeLevel(user.getLevel());
		StreakUpdate streakUpdate = updateStreak(user, resolveCompletionDate(attempt));
		long completedChallenges = challengeAttemptRepository.countDistinctCompletedChallengesByUserId(user.getId());
		List<Badge> unlockedBadges = unlockBadges(
				user,
				firstCompletion,
				earnedScore,
				maxScore,
				completedChallenges,
				updatedTotalPoints,
				streakUpdate.currentStreak()
		);
		List<Notification> notifications = new ArrayList<>();
		notifications.add(buildNotification(
				user,
				"Challenge completed",
				awardedPoints > 0
						? "Great work! You completed \"" + challenge.getTitle() + "\" and earned " + awardedPoints + " points."
						: "Challenge complete. Your latest attempt on \"" + challenge.getTitle() + "\" has been recorded.",
				"CHALLENGE_COMPLETED"
		));

		if (updatedLevel > previousLevel) {
			notifications.add(buildNotification(
					user,
					"Level up!",
					"You reached level " + updatedLevel + ". Keep the momentum going.",
					"LEVEL_UP"
			));
		}

		if (streakUpdate.milestoneReached()) {
			notifications.add(buildNotification(
					user,
					"Streak unlocked",
					"You reached a " + streakUpdate.currentStreak() + "-day learning streak. Stay consistent.",
					"STREAK_REACHED"
			));
		}

		for (Badge badge : unlockedBadges) {
			notifications.add(buildNotification(
					user,
					"Badge unlocked",
					"You unlocked the \"" + badge.getName() + "\" badge. Nice progress.",
					"BADGE_UNLOCKED"
			));
		}

		userRepository.save(user);
		notificationRepository.saveAll(notifications);

		return new ChallengeGamificationUpdate(
				awardedPoints,
				updatedTotalPoints,
				updatedLevel,
				streakUpdate.currentStreak(),
				resolveRank(user.getId()),
				unlockedBadges.stream().map(badge -> toBadgeResponse(badge, null, true)).toList(),
				notifications.stream().map(this::toNotificationResponse).toList()
		);
	}

	@Transactional(readOnly = true)
	public GamificationMeResponse getMyGamification(User currentUser) {
		User user = getManagedUser(currentUser);
		long completedChallenges = challengeAttemptRepository.countDistinctCompletedChallengesByUserId(user.getId());
		List<UserBadge> earnedBadgeRecords = userBadgeRepository.findByUser_IdOrderByEarnedAtDesc(user.getId());
		Map<Long, UserBadge> earnedByBadgeId = new LinkedHashMap<>();

		for (UserBadge userBadge : earnedBadgeRecords) {
			earnedByBadgeId.putIfAbsent(userBadge.getBadge().getId(), userBadge);
		}

		List<BadgeResponse> earnedBadges = new ArrayList<>();
		List<BadgeResponse> lockedBadges = new ArrayList<>();

		for (Badge badge : badgeRepository.findAllByOrderByConditionValueAscNameAsc()) {
			UserBadge earnedRecord = earnedByBadgeId.get(badge.getId());
			BadgeResponse response = toBadgeResponse(badge, earnedRecord, earnedRecord != null);
			if (earnedRecord != null) {
				earnedBadges.add(response);
			} else {
				lockedBadges.add(response);
			}
		}

		int totalPoints = safePoints(user.getPointsTotal());
		int level = safeLevel(user.getLevel());
		int levelFloor = Math.max(0, (level - 1) * LEVEL_STEP_POINTS);
		int nextLevelTarget = level * LEVEL_STEP_POINTS;
		int progressCurrent = Math.max(0, totalPoints - levelFloor);
		int progressMax = LEVEL_STEP_POINTS;
		int pointsToNextLevel = Math.max(0, nextLevelTarget - totalPoints);
		int progressPercentage = progressMax == 0 ? 0 : Math.min(100, Math.round((progressCurrent * 100.0f) / progressMax));
		Integer currentRank = resolveRank(user.getId());

		return new GamificationMeResponse(
				user.getId(),
				user.getUsername(),
				totalPoints,
				level,
				nextLevelTarget,
				progressCurrent,
				progressMax,
				progressPercentage,
				pointsToNextLevel,
				(int) completedChallenges,
				earnedBadges.size(),
				safeStreak(user.getStreakCount()),
				user.getLastActivityDate(),
				currentRank,
				resolveTopBadgeLabel(earnedBadgeRecords),
				earnedBadges,
				lockedBadges,
				notificationRepository.findTop6ByUser_IdOrderByCreatedAtDesc(user.getId()).stream().map(this::toNotificationResponse).toList(),
				mapRecentActivity(user.getId()),
				buildLeaderboardEntries(5)
		);
	}

	@Transactional(readOnly = true)
	public LeaderboardResponse getLeaderboard(User currentUser) {
		User user = getManagedUser(currentUser);
		return new LeaderboardResponse(buildLeaderboardEntries(10), resolveRank(user.getId()));
	}

	@Transactional(readOnly = true)
	public List<BadgeResponse> getAvailableBadges(User currentUser) {
		User user = getManagedUser(currentUser);
		Map<Long, UserBadge> earnedByBadgeId = new LinkedHashMap<>();

		for (UserBadge userBadge : userBadgeRepository.findByUser_IdOrderByEarnedAtDesc(user.getId())) {
			earnedByBadgeId.putIfAbsent(userBadge.getBadge().getId(), userBadge);
		}

		return badgeRepository.findAllByOrderByConditionValueAscNameAsc()
				.stream()
				.map(badge -> toBadgeResponse(badge, earnedByBadgeId.get(badge.getId()), earnedByBadgeId.containsKey(badge.getId())))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<BadgeResponse> getMyEarnedBadges(User currentUser) {
		User user = getManagedUser(currentUser);
		return userBadgeRepository.findByUser_IdOrderByEarnedAtDesc(user.getId())
				.stream()
				.map(userBadge -> toBadgeResponse(userBadge.getBadge(), userBadge, true))
				.toList();
	}

	private List<LeaderboardEntryResponse> buildLeaderboardEntries(int limit) {
		List<UserRepository.LeaderboardProjection> projections = userRepository.findLeaderboardEntries(limit);
		List<Long> userIds = projections.stream().map(UserRepository.LeaderboardProjection::getId).toList();
		Map<Long, String> badgeLabels = resolveLeaderboardBadgeLabels(userIds);
		List<LeaderboardEntryResponse> entries = new ArrayList<>();

		for (UserRepository.LeaderboardProjection projection : projections) {
			entries.add(new LeaderboardEntryResponse(
					projection.getId(),
					projection.getRankPosition(),
					projection.getUsername(),
					safePoints(projection.getPointsTotal()),
					"Level " + safeLevel(projection.getLevel()),
					badgeLabels.getOrDefault(projection.getId(), "No badge yet")
			));
		}

		return entries;
	}

	private Map<Long, String> resolveLeaderboardBadgeLabels(List<Long> userIds) {
		Map<Long, String> labels = new LinkedHashMap<>();

		if (userIds == null || userIds.isEmpty()) {
			return labels;
		}

		for (UserBadge userBadge : userBadgeRepository.findByUser_IdInOrderByEarnedAtDesc(userIds)) {
			Long userId = userBadge.getUser().getId();
			labels.putIfAbsent(userId, userBadge.getBadge().getName());
		}

		return labels;
	}

	private List<RecentChallengeActivityResponse> mapRecentActivity(Long userId) {
		return challengeAttemptRepository.findTop6ByUser_IdAndStatusIgnoreCaseOrderByCompletedAtDesc(userId, "COMPLETED")
				.stream()
				.map(attempt -> {
					int maxScore = resolveMaxScore(attempt.getChallenge());
					return new RecentChallengeActivityResponse(
							attempt.getId(),
							attempt.getChallenge().getId(),
							attempt.getChallenge().getTitle(),
							attempt.getScore(),
							maxScore,
							attempt.getCompletedAt(),
							attempt.getStatus(),
							resolveXpEarned(attempt, maxScore),
							resolveOutcomeLabel(attempt)
					);
				})
				.toList();
	}

	private int resolveXpEarned(ChallengeAttempt attempt, int maxScore) {
		if (attempt.getId() == null
				|| attempt.getUser() == null
				|| attempt.getUser().getId() == null
				|| attempt.getChallenge() == null
				|| attempt.getChallenge().getId() == null) {
			return 0;
		}

		boolean earlierCompletionExists = challengeAttemptRepository.existsByUser_IdAndChallenge_IdAndStatusIgnoreCaseAndIdLessThan(
				attempt.getUser().getId(),
				attempt.getChallenge().getId(),
				"COMPLETED",
				attempt.getId()
		);

		if (earlierCompletionExists) {
			return 0;
		}

		return calculateAwardedPoints(
				attempt.getScore() == null ? 0 : attempt.getScore(),
				maxScore,
				attempt.getChallenge().getPointsReward()
		);
	}

	private String resolveOutcomeLabel(ChallengeAttempt attempt) {
		int score = attempt.getScore() == null ? 0 : attempt.getScore();
		int maxScore = resolveMaxScore(attempt.getChallenge());

		if (maxScore > 0 && score >= maxScore) {
			return "Perfect score";
		}

		return "Completed";
	}

	private int resolveMaxScore(Challenge challenge) {
		if (challenge.getSteps() == null) {
			return 0;
		}

		int total = 0;
		for (ChallengeStep step : challenge.getSteps()) {
			total += step.getPoints() == null ? 1 : Math.max(1, step.getPoints());
		}
		return total;
	}

	private List<Badge> unlockBadges(
			User user,
			boolean firstCompletion,
			int earnedScore,
			int maxScore,
			long completedChallenges,
			int totalPoints,
			int currentStreak
	) {
		List<Badge> unlocked = new ArrayList<>();

		for (Badge badge : badgeRepository.findAllByOrderByConditionValueAscNameAsc()) {
			if (userBadgeRepository.existsByUser_IdAndBadge_Id(user.getId(), badge.getId())) {
				continue;
			}

			if (matchesBadgeCondition(badge, firstCompletion, earnedScore, maxScore, completedChallenges, totalPoints, currentStreak)) {
				UserBadge userBadge = new UserBadge();
				userBadge.setUser(user);
				userBadge.setBadge(badge);
				userBadgeRepository.save(userBadge);
				unlocked.add(badge);
			}
		}

		return unlocked;
	}

	private boolean matchesBadgeCondition(
			Badge badge,
			boolean firstCompletion,
			int earnedScore,
			int maxScore,
			long completedChallenges,
			int totalPoints,
			int currentStreak
	) {
		String conditionType = badge.getConditionType() == null ? "" : badge.getConditionType().trim().toUpperCase();
		int conditionValue = badge.getConditionValue() == null ? 0 : badge.getConditionValue();
		boolean perfectScore = maxScore > 0 && earnedScore >= maxScore;

		return switch (conditionType) {
			case "CHALLENGES_COMPLETED" -> completedChallenges >= conditionValue;
			case "POINTS_REACHED" -> totalPoints >= conditionValue;
			case "PERFECT_SCORE" -> perfectScore;
			case "FIRST_TRY_PERFECT" -> firstCompletion && perfectScore;
			case "STREAK_REACHED" -> currentStreak >= conditionValue;
			default -> false;
		};
	}

	private int calculateAwardedPoints(int earnedScore, int maxScore, Integer challengeReward) {
		if (earnedScore <= 0 || maxScore <= 0 || challengeReward == null || challengeReward <= 0) {
			return 0;
		}

		return (int) Math.round(((double) earnedScore / maxScore) * challengeReward);
	}

	private int applyAwardedPoints(User user, int awardedPoints) {
		int updatedPoints = safePoints(user.getPointsTotal()) + Math.max(0, awardedPoints);
		user.setPointsTotal(updatedPoints);
		user.setLevel(calculateLevel(updatedPoints));
		return updatedPoints;
	}

	private int calculateLevel(int totalPoints) {
		return Math.max(1, (Math.max(0, totalPoints) / LEVEL_STEP_POINTS) + 1);
	}

	private StreakUpdate updateStreak(User user, LocalDate activityDate) {
		LocalDate lastActivityDate = user.getLastActivityDate();
		int currentStreak = safeStreak(user.getStreakCount());
		boolean milestoneReached = false;

		if (activityDate == null) {
			return new StreakUpdate(currentStreak, false);
		}

		if (lastActivityDate == null) {
			currentStreak = 1;
			milestoneReached = isStreakMilestone(currentStreak);
		} else if (lastActivityDate.equals(activityDate)) {
			milestoneReached = false;
		} else if (lastActivityDate.plusDays(1).equals(activityDate)) {
			currentStreak += 1;
			milestoneReached = isStreakMilestone(currentStreak);
		} else {
			currentStreak = 1;
			milestoneReached = isStreakMilestone(currentStreak);
		}

		user.setLastActivityDate(activityDate);
		user.setStreakCount(currentStreak);
		return new StreakUpdate(currentStreak, milestoneReached);
	}

	private boolean isStreakMilestone(int streak) {
		return streak == 3 || streak == 7;
	}

	private LocalDate resolveCompletionDate(ChallengeAttempt attempt) {
		LocalDateTime completedAt = attempt.getCompletedAt() != null ? attempt.getCompletedAt() : attempt.getStartedAt();
		return completedAt == null ? LocalDate.now() : completedAt.toLocalDate();
	}

	private Notification buildNotification(User user, String title, String message, String type) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setNotificationType(type);
		return notification;
	}

	private NotificationResponse toNotificationResponse(Notification notification) {
		return new NotificationResponse(
				notification.getId(),
				notification.getTitle(),
				notification.getMessage(),
				notification.getNotificationType(),
				notification.getIsRead(),
				notification.getCreatedAt()
		);
	}

	private BadgeResponse toBadgeResponse(Badge badge, UserBadge userBadge, boolean earned) {
		return new BadgeResponse(
				badge.getId(),
				badge.getSlug(),
				badge.getName(),
				badge.getDescription(),
				badge.getBadgeType(),
				earned ? badge.getTone() : "locked",
				badge.getIconName(),
				badge.getConditionType(),
				badge.getConditionValue(),
				earned,
				userBadge != null ? userBadge.getEarnedAt() : null
		);
	}

	private String resolveTopBadgeLabel(List<UserBadge> earnedBadgeRecords) {
		if (earnedBadgeRecords == null || earnedBadgeRecords.isEmpty()) {
			return "No badge yet";
		}

		return earnedBadgeRecords.get(0).getBadge().getName();
	}

	private Integer resolveRank(Long userId) {
		return userId == null ? null : userRepository.findRankPositionByUserId(userId);
	}

	private User getManagedUser(User currentUser) {
		if (currentUser.getId() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found");
		}

		return userRepository.findById(currentUser.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
	}

	private int safePoints(Integer points) {
		return points == null ? 0 : Math.max(0, points);
	}

	private int safeLevel(Integer level) {
		return level == null ? 1 : Math.max(1, level);
	}

	private int safeStreak(Integer streak) {
		return streak == null ? 0 : Math.max(0, streak);
	}

	public record ChallengeGamificationUpdate(
			int awardedPoints,
			int totalPoints,
			int level,
			int currentStreak,
			Integer currentRank,
			List<BadgeResponse> unlockedBadges,
			List<NotificationResponse> notifications
	) {
	}

	private record StreakUpdate(int currentStreak, boolean milestoneReached) {
	}
}
