import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import AttemptHistoryTable from '../components/AttemptHistoryTable'
import BadgeCard from '../components/BadgeCard'
import ProgressBar from '../components/ProgressBar'
import { useAuth } from '../context/AuthContext'
import { fetchMyGamification } from '../services/gamification'

function formatDate(value, fallback = 'Not available yet') {
  if (!value) {
    return fallback
  }

  try {
    return new Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    }).format(new Date(value))
  } catch {
    return value
  }
}

function ProfilePage() {
  const { user, isAuthenticated, syncGamificationSnapshot } = useAuth()
  const [reloadKey, setReloadKey] = useState(0)
  const [loadingState, setLoadingState] = useState(isAuthenticated ? 'loading' : 'idle')
  const [gamification, setGamification] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const userId = user?.id
  const userEmail = user?.email

  useEffect(() => {
    if (!isAuthenticated || !user) {
      setLoadingState('idle')
      setGamification(null)
      setErrorMessage('')
      return
    }

    let active = true

    async function loadProfileGamification() {
      setLoadingState('loading')
      setErrorMessage('')

      try {
        const snapshot = await fetchMyGamification(user)

        if (!active) {
          return
        }

        setGamification(snapshot)
        setLoadingState('done')
        syncGamificationSnapshot(snapshot)
      } catch (error) {
        if (!active) {
          return
        }

        setLoadingState('error')
        setErrorMessage(error.message || 'Unable to load your profile gamification data.')
      }
    }

    loadProfileGamification()

    return () => {
      active = false
    }
  }, [isAuthenticated, reloadKey, userEmail, userId])

  if (!isAuthenticated) {
    return (
      <section className="section">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">Profile gamification</span>
          <h1>Login to view your badges, progress history, and streak.</h1>
          <p>The profile page is personalized from the gamification APIs after authentication.</p>
          <div className="details-actions">
            <Link className="button-primary" to="/login">
              Login
            </Link>
            <Link className="button-secondary" to="/register">
              Create account
            </Link>
          </div>
        </article>
      </section>
    )
  }

  if (loadingState === 'loading' && !gamification) {
    return <section className="empty-state">Loading profile data...</section>
  }

  if (!gamification) {
    return (
      <section className="section">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">Profile unavailable</span>
          <h1>Profile data could not be loaded.</h1>
          <p>{errorMessage || 'Please try again in a moment.'}</p>
          <button className="button-primary" onClick={() => setReloadKey((value) => value + 1)} type="button">
            Retry
          </button>
        </article>
      </section>
    )
  }

  const initials = (gamification.username || 'ED')
    .split(/[.\s_-]+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  return (
    <>
      {loadingState === 'error' && (
        <section className="section">
          <div className="error-banner">
            {errorMessage}
            <div className="details-actions" style={{ marginTop: '0.8rem' }}>
              <button className="button-secondary" onClick={() => setReloadKey((value) => value + 1)} type="button">
                Retry
              </button>
            </div>
          </div>
        </section>
      )}

      <section className="profile-grid section">
        <article className="profile-card">
          <div className="profile-header">
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
              <div className="avatar">{initials}</div>
              <div>
                <h1 style={{ margin: 0 }}>{gamification.username}</h1>
                <p className="muted-caption">
                  {user?.email || 'Connected learner'} | Level {gamification.level}
                </p>
              </div>
            </div>
            <span className="tag">
              {gamification.currentRank ? `Rank #${gamification.currentRank}` : 'Not ranked yet'}
            </span>
          </div>

          <p style={{ marginTop: '1rem' }}>
            Keep building your streak, unlock badges, and improve your position on the
            EduChallenge leaderboard.
          </p>

          <div className="info-grid" style={{ marginTop: '1.4rem' }}>
            <div className="info-tile">
              <strong>{gamification.totalPoints.toLocaleString()}</strong>
              <p className="muted-caption">Total points</p>
            </div>
            <div className="info-tile">
              <strong>{gamification.earnedBadgesCount}</strong>
              <p className="muted-caption">Earned badges</p>
            </div>
            <div className="info-tile">
              <strong>{gamification.completedChallenges}</strong>
              <p className="muted-caption">Completed challenges</p>
            </div>
            <div className="info-tile">
              <strong>{gamification.currentStreak}</strong>
              <p className="muted-caption">Current streak</p>
            </div>
          </div>
        </article>

        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Current level</span>
              <h2>Level {gamification.level}</h2>
              <p>
                {gamification.pointsToNextLevel === 0
                  ? 'You already unlocked the next level threshold.'
                  : `${gamification.pointsToNextLevel} more points required to level up.`}
              </p>
            </div>
          </div>

          <ProgressBar
            label="Progress to next level"
            current={gamification.progressCurrent}
            max={gamification.progressMax}
            helper={`${gamification.progressPercentage}% complete`}
          />

          <div className="info-grid" style={{ marginTop: '1.4rem' }}>
            <div className="info-tile">
              <strong>{gamification.topBadge}</strong>
              <p className="muted-caption">Top badge</p>
            </div>
            <div className="info-tile">
              <strong>{formatDate(gamification.lastActivityDate)}</strong>
              <p className="muted-caption">Last activity date</p>
            </div>
          </div>
        </article>
      </section>

      <section className="dashboard-grid profile-badges-grid section">
        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Earned badges</span>
              <h2>Unlocked rewards</h2>
              <p>These badges are already part of your collection.</p>
            </div>
          </div>

          {gamification.earnedBadges.length > 0 ? (
            <div className="badge-grid">
              {gamification.earnedBadges.map((badge) => (
                <BadgeCard key={badge.id || badge.slug || badge.title} badge={badge} />
              ))}
            </div>
          ) : (
            <div className="empty-state">
              No badges yet. Finish your first challenge to unlock the first reward.
            </div>
          )}
        </article>

        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Locked badges</span>
              <h2>Next rewards to unlock</h2>
              <p>Visible milestones help the learner know what to chase next.</p>
            </div>
          </div>

          {gamification.lockedBadges.length > 0 ? (
            <div className="badge-grid">
              {gamification.lockedBadges.map((badge) => (
                <BadgeCard key={badge.id || badge.slug || badge.title} badge={badge} />
              ))}
            </div>
          ) : (
            <div className="empty-state">All available badges are already unlocked.</div>
          )}
        </article>
      </section>

      <section className="dashboard-grid section">
        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Participation history</span>
              <h2>Attempt history</h2>
              <p>Challenge name, score, date, status, and XP earned for each recent completed attempt.</p>
            </div>
          </div>

          {gamification.recentActivity.length > 0 ? (
            <AttemptHistoryTable attempts={gamification.recentActivity} />
          ) : (
            <div className="empty-state">
              No participation history yet. Complete challenges to build this timeline.
            </div>
          )}
        </article>

        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Recent achievements</span>
              <h2>Gamified notifications</h2>
              <p>Challenge completions, new badges, level-ups, and streaks appear here.</p>
            </div>
          </div>

          {gamification.recentAchievements.length > 0 ? (
            <div className="achievement-list">
              {gamification.recentAchievements.map((achievement) => (
                <div className="achievement-item" key={achievement.id}>
                  <div>
                    <strong>{achievement.title}</strong>
                    <p className="muted-caption">{achievement.message}</p>
                  </div>
                  <span className="achievement-type">{achievement.notificationType}</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">No notifications yet. Your next milestone will appear here.</div>
          )}
        </article>
      </section>
    </>
  )
}

export default ProfilePage
