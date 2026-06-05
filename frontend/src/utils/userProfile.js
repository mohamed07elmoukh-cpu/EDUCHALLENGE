export function createInitialUserProfile({
  id = null,
  username = 'new.user',
  email = '',
  role = 'USER',
  accessToken = null,
  tokenType = 'Bearer',
  points = 0,
  level = 1,
  streak = 0,
  badges = 0,
  completedChallenges = 0,
  rank = null,
  nextLevelPoints = 100,
  topBadge = 'No badge yet',
} = {}) {
  return {
    id,
    name: username,
    username,
    email,
    role,
    accessToken,
    tokenType,
    level,
    joinedAt: 'Just joined',
    school: 'New learner',
    bio: 'Ready to start the first educational challenge.',
    points,
    completedChallenges,
    badges,
    rank,
    streak,
    topBadge,
    nextLevelPoints,
    lastActivityDate: null,
  }
}

export function mapBackendUserToProfile(user) {
  return createInitialUserProfile({
    id: user?.id ?? null,
    username: user?.username ?? 'new.user',
    email: user?.email ?? '',
    role: user?.role ?? 'USER',
    accessToken: user?.accessToken ?? null,
    tokenType: user?.tokenType ?? 'Bearer',
    points: user?.pointsTotal ?? 0,
    level: user?.level ?? 1,
  })
}
