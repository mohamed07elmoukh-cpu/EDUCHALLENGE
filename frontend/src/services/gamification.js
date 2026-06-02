import { API_BASE_URL, buildAuthHeaders, parseResponse } from './api'

function toNumber(value, fallback = 0) {
  const parsedValue = Number(value)
  return Number.isFinite(parsedValue) ? parsedValue : fallback
}

function ensureArray(value) {
  return Array.isArray(value) ? value : []
}

function normalizeBadge(badge) {
  return {
    ...badge,
    id: badge?.id ?? null,
    title: badge?.title || 'Badge',
    description: badge?.description || '',
    badgeType: badge?.badgeType || 'achievement',
    tone: badge?.tone || (badge?.earned ? 'bronze' : 'locked'),
    earned: Boolean(badge?.earned),
    conditionValue: toNumber(badge?.conditionValue, 0),
  }
}

function normalizeLeaderboardEntry(entry) {
  return {
    ...entry,
    id: entry?.id ?? null,
    rank: toNumber(entry?.rank, 0),
    username: entry?.username || 'unknown.user',
    points: toNumber(entry?.points, 0),
    level: entry?.level || 'Level 1',
    badge: entry?.badge || 'No badge yet',
  }
}

function normalizeRecentActivity(item) {
  return {
    ...item,
    attemptId: item?.attemptId ?? null,
    challengeId: item?.challengeId ?? null,
    challengeTitle: item?.challengeTitle || 'Challenge',
    score: toNumber(item?.score, 0),
    maxScore: toNumber(item?.maxScore, 0),
    outcomeLabel: item?.outcomeLabel || 'Completed',
  }
}

function normalizeNotification(notification) {
  return {
    ...notification,
    id: notification?.id ?? null,
    title: notification?.title || 'Notification',
    message: notification?.message || '',
    notificationType: notification?.notificationType || 'GENERAL',
    isRead: Boolean(notification?.isRead),
  }
}

function normalizeGamificationSnapshot(snapshot) {
  return {
    ...snapshot,
    userId: snapshot?.userId ?? null,
    username: snapshot?.username || 'new.user',
    totalPoints: toNumber(snapshot?.totalPoints, 0),
    level: toNumber(snapshot?.level, 1),
    nextLevelTarget: toNumber(snapshot?.nextLevelTarget, 100),
    progressCurrent: toNumber(snapshot?.progressCurrent, 0),
    progressMax: Math.max(1, toNumber(snapshot?.progressMax, 100)),
    progressPercentage: toNumber(snapshot?.progressPercentage, 0),
    pointsToNextLevel: toNumber(snapshot?.pointsToNextLevel, 0),
    completedChallenges: toNumber(snapshot?.completedChallenges, 0),
    earnedBadgesCount: toNumber(snapshot?.earnedBadgesCount, 0),
    currentStreak: toNumber(snapshot?.currentStreak, 0),
    currentRank: snapshot?.currentRank == null ? null : toNumber(snapshot.currentRank, null),
    topBadge: snapshot?.topBadge || 'No badge yet',
    earnedBadges: ensureArray(snapshot?.earnedBadges).map(normalizeBadge),
    lockedBadges: ensureArray(snapshot?.lockedBadges).map(normalizeBadge),
    recentAchievements: ensureArray(snapshot?.recentAchievements).map(normalizeNotification),
    recentActivity: ensureArray(snapshot?.recentActivity).map(normalizeRecentActivity),
    miniLeaderboard: ensureArray(snapshot?.miniLeaderboard).map(normalizeLeaderboardEntry),
  }
}

async function fetchGamificationResource(path, user, defaultErrorMessage) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: buildAuthHeaders(user),
  })
  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || defaultErrorMessage)
    error.status = response.status
    throw error
  }

  return data
}

export async function fetchMyGamification(user) {
  const data = await fetchGamificationResource(
    '/api/gamification/me',
    user,
    'Unable to load gamification overview',
  )

  return normalizeGamificationSnapshot(data)
}

export async function fetchLeaderboard(user) {
  const data = await fetchGamificationResource(
    '/api/leaderboard',
    user,
    'Unable to load leaderboard',
  )

  return {
    entries: ensureArray(data?.entries).map(normalizeLeaderboardEntry),
    currentUserRank:
      data?.currentUserRank == null ? null : toNumber(data.currentUserRank, null),
  }
}

export async function fetchAvailableBadges(user) {
  const data = await fetchGamificationResource(
    '/api/badges',
    user,
    'Unable to load badges',
  )

  return ensureArray(data).map(normalizeBadge)
}

export async function fetchMyBadges(user) {
  const data = await fetchGamificationResource(
    '/api/users/me/badges',
    user,
    'Unable to load earned badges',
  )

  return ensureArray(data).map(normalizeBadge)
}
