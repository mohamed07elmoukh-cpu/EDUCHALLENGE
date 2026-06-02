import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { createInitialUserProfile, mapBackendUserToProfile } from '../utils/userProfile'

const STORAGE_KEY = 'educhallenge-auth'
const REGISTERED_USER_KEY = 'educhallenge-registered-user'

const AuthContext = createContext(null)

function calculateNextLevelPoints(level) {
  const safeLevel = Number.isFinite(level) && level > 0 ? level : 1
  return safeLevel * 100
}

function syncRegisteredUserProgress(profileUser) {
  const rawRegisteredUser = window.localStorage.getItem(REGISTERED_USER_KEY)

  if (!rawRegisteredUser) {
    return
  }

  try {
    const registeredUser = JSON.parse(rawRegisteredUser)
    const sameUser =
      (profileUser.id && registeredUser.id && profileUser.id === registeredUser.id) ||
      (profileUser.email &&
        registeredUser.email &&
        profileUser.email.toLowerCase() === registeredUser.email.toLowerCase())

    if (!sameUser) {
      return
    }

    window.localStorage.setItem(
      REGISTERED_USER_KEY,
      JSON.stringify({
        ...registeredUser,
        pointsTotal: profileUser.points ?? 0,
        level: profileUser.level ?? 1,
      }),
    )
  } catch {
    window.localStorage.removeItem(REGISTERED_USER_KEY)
  }
}

function mergeGamificationProfile(profileUser, snapshot) {
  if (!snapshot) {
    return profileUser
  }

  const nextLevel = snapshot.level ?? profileUser.level ?? 1

  return {
    ...profileUser,
    points: snapshot.totalPoints ?? profileUser.points ?? 0,
    level: nextLevel,
    completedChallenges:
      snapshot.completedChallenges ?? profileUser.completedChallenges ?? 0,
    badges:
      snapshot.earnedBadgesCount ??
      snapshot.earnedBadges?.length ??
      profileUser.badges ??
      0,
    rank: snapshot.currentRank ?? profileUser.rank ?? null,
    streak: snapshot.currentStreak ?? profileUser.streak ?? 0,
    topBadge: snapshot.topBadge ?? profileUser.topBadge ?? 'No badge yet',
    nextLevelPoints:
      snapshot.nextLevelTarget ??
      profileUser.nextLevelPoints ??
      calculateNextLevelPoints(nextLevel),
    lastActivityDate: snapshot.lastActivityDate ?? profileUser.lastActivityDate ?? null,
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const rawValue = window.localStorage.getItem(STORAGE_KEY)

    if (!rawValue) {
      return
    }

    try {
      setUser(JSON.parse(rawValue))
    } catch {
      window.localStorage.removeItem(STORAGE_KEY)
    }
  }, [])

  const persistAuthenticatedUser = (backendUser) => {
    const nextUser = mapBackendUserToProfile(backendUser)
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextUser))
    window.localStorage.setItem(REGISTERED_USER_KEY, JSON.stringify(backendUser))
    setUser(nextUser)
    return nextUser
  }

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isAdmin: user?.role === 'ADMIN',
      login(backendUserOrEmail) {
        if (backendUserOrEmail && typeof backendUserOrEmail === 'object') {
          return persistAuthenticatedUser(backendUserOrEmail)
        }

        const email = backendUserOrEmail
        const rawRegisteredUser = window.localStorage.getItem(REGISTERED_USER_KEY)
        let nextUser = null

        if (rawRegisteredUser) {
          try {
            const registeredUser = JSON.parse(rawRegisteredUser)

            if (!email || registeredUser.email?.toLowerCase() === email.toLowerCase()) {
              nextUser = mapBackendUserToProfile(registeredUser)
            }
          } catch {
            window.localStorage.removeItem(REGISTERED_USER_KEY)
          }
        }

        if (!nextUser) {
          const username = email ? email.split('@')[0] : 'new.user'
          nextUser = createInitialUserProfile({ username, email: email || '' })
        }

        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextUser))
        setUser(nextUser)
        return nextUser
      },
      rememberRegisteredUser(backendUser) {
        window.localStorage.setItem(REGISTERED_USER_KEY, JSON.stringify(backendUser))
      },
      applyChallengeAttemptResult(result) {
        setUser((currentUser) => {
          if (!currentUser) {
            return currentUser
          }

          const nextPoints = result?.totalPoints ?? currentUser.points ?? 0
          const nextLevel = result?.level ?? currentUser.level ?? 1
          const nextCompletedChallenges =
            result?.firstCompletion
              ? (currentUser.completedChallenges ?? 0) + 1
              : currentUser.completedChallenges ?? 0
          const nextBadgesCount =
            (currentUser.badges ?? 0) + ((result?.unlockedBadges || []).length || 0)
          const nextUser = {
            ...currentUser,
            points: nextPoints,
            level: nextLevel,
            completedChallenges: nextCompletedChallenges,
            badges: nextBadgesCount,
            rank: result?.currentRank ?? currentUser.rank ?? null,
            streak: result?.currentStreak ?? currentUser.streak ?? 0,
            nextLevelPoints: calculateNextLevelPoints(nextLevel),
          }

          window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextUser))
          syncRegisteredUserProgress(nextUser)
          return nextUser
        })
      },
      syncGamificationSnapshot(snapshot) {
        setUser((currentUser) => {
          if (!currentUser) {
            return currentUser
          }

          const nextUser = mergeGamificationProfile(currentUser, snapshot)
          window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextUser))
          syncRegisteredUserProgress(nextUser)
          return nextUser
        })
      },
      logout() {
        window.localStorage.removeItem(STORAGE_KEY)
        setUser(null)
      },
    }),
    [user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }

  return context
}
