import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import BadgeCard from '../components/BadgeCard'
import LeaderboardTable from '../components/LeaderboardTable'
import ProgressBar from '../components/ProgressBar'
import StatCard from '../components/StatCard'
import { ChartIcon, MedalIcon, RocketIcon, TargetIcon, TrophyIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import { fetchMyGamification } from '../services/gamification'

const statIcons = [
  <RocketIcon key="rocket" />,
  <ChartIcon key="chart" />,
  <MedalIcon key="medal" />,
  <TargetIcon key="target" />,
  <TrophyIcon key="trophy" />,
]

function formatDate(value) {
  if (!value) {
    return 'Today'
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

function DashboardPage() {
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

    async function loadGamification() {
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
        setErrorMessage(error.message || 'Unable to load the dashboard right now.')
      }
    }

    loadGamification()

    return () => {
      active = false
    }
  }, [isAuthenticated, reloadKey, userEmail, userId])

  if (!isAuthenticated) {
    return (
      <section className="section">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">Gamification dashboard</span>
          <h1>Sign in to view your points, streak, badges, and ranking.</h1>
          <p>
            The dashboard becomes personalized once the user is connected and the backend
            can load the current gamification state.
          </p>
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
    return <section className="empty-state">Loading your gamification dashboard...</section>
  }

  if (!gamification) {
    return (
      <section className="section">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">Dashboard unavailable</span>
          <h1>Gamification data could not be loaded.</h1>
          <p>{errorMessage || 'Please try again in a moment.'}</p>
          <button className="button-primary" onClick={() => setReloadKey((value) => value + 1)} type="button">
            Retry
          </button>
        </article>
      </section>
    )
  }

  const stats = [
    {
      label: 'Total points',
      value: gamification.totalPoints.toLocaleString(),
      hint: 'Points earned across completed challenges',
    },
    {
      label: 'Current level',
      value: `Level ${gamification.level}`,
      hint: `${gamification.pointsToNextLevel} points to the next level`,
    },
    {
      label: 'Earned badges',
      value: gamification.earnedBadgesCount.toString(),
      hint: gamification.topBadge || 'No badge yet',
    },
    {
      label: 'Current streak',
      value: `${gamification.currentStreak} day(s)`,
      hint: gamification.lastActivityDate
        ? `Last active on ${formatDate(gamification.lastActivityDate)}`
        : 'Complete a challenge to start a streak',
    },
    {
      label: 'Current rank',
      value: gamification.currentRank ? `#${gamification.currentRank}` : 'Not ranked yet',
      hint: 'Ranked by points, level, and username',
    },
  ]

  return (
    <>
      <section className="section">
        <article className="surface-card welcome-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Gamification overview</span>
              <h1>Welcome back, {gamification.username}</h1>
              <p>
                Track your points, maintain your streak, unlock badges, and compare your
                performance with the top learners in real time.
              </p>
            </div>
            <div className="welcome-card-aside">
              <strong>{gamification.totalPoints.toLocaleString()} pts</strong>
              <span>
                Level {gamification.level}
                {gamification.currentRank ? ` | Rank #${gamification.currentRank}` : ''}
              </span>
            </div>
          </div>
        </article>
      </section>

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

      <section className="section stat-grid stat-grid-wide">
        {stats.map((stat, index) => (
          <StatCard
            key={stat.label}
            icon={statIcons[index]}
            label={stat.label}
            value={stat.value}
            hint={stat.hint}
          />
        ))}
      </section>

      <section className="dashboard-grid section">
        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Progress to next level</span>
              <h2>Level {gamification.level}</h2>
              <p>
                {gamification.pointsToNextLevel === 0
                  ? 'You already reached the next level threshold.'
                  : `${gamification.pointsToNextLevel} more points to level up.`}
              </p>
            </div>
          </div>

          <ProgressBar
            label="Level progress"
            current={gamification.progressCurrent}
            max={gamification.progressMax}
            helper={`${gamification.progressPercentage}% completed toward level ${gamification.level + 1}`}
          />

          <div className="info-grid" style={{ marginTop: '1.4rem' }}>
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
              <span className="eyebrow">Recent achievements</span>
              <h2>Notifications & milestones</h2>
              <p>Automatic updates after challenges, badges, levels, and streak milestones.</p>
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
            <div className="empty-state">
              No achievements yet. Complete a challenge to trigger your first gamified update.
            </div>
          )}
        </article>
      </section>

      <section className="dashboard-grid section">
        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Mini leaderboard</span>
              <h2>Top learners right now</h2>
              <p>See the competition and track where your profile currently stands.</p>
            </div>
            <Link className="button-secondary" to="/leaderboard">
              Full leaderboard
            </Link>
          </div>

          {gamification.miniLeaderboard.length > 0 ? (
            <LeaderboardTable
              currentUserId={gamification.userId}
              entries={gamification.miniLeaderboard}
            />
          ) : (
            <div className="empty-state">Leaderboard data is not available yet.</div>
          )}
        </article>

        <article className="surface-card">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Recent activity</span>
              <h2>Latest completed challenges</h2>
              <p>Your last finished attempts appear here with score and completion date.</p>
            </div>
          </div>

          {gamification.recentActivity.length > 0 ? (
            <div className="history-list">
              {gamification.recentActivity.map((item) => (
                <div className="history-item" key={item.attemptId}>
                  <div>
                    <strong>{item.challengeTitle}</strong>
                    <p className="muted-caption">{formatDate(item.completedAt)}</p>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <strong>{item.outcomeLabel}</strong>
                    <p className="muted-caption">
                      {item.score}/{item.maxScore}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              No completed challenges yet. Finish your first challenge to populate this feed.
            </div>
          )}
        </article>
      </section>

      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Badges</span>
            <h2>Earned badges</h2>
            <p>Unlocked badges are shown first, with locked rewards still visible for motivation.</p>
          </div>
          <Link className="button-secondary" to="/profile">
            Open profile
          </Link>
        </div>

        {gamification.earnedBadges.length > 0 ? (
          <div className="badge-grid">
            {gamification.earnedBadges.slice(0, 3).map((badge) => (
              <BadgeCard key={badge.id || badge.slug || badge.title} badge={badge} />
            ))}
          </div>
        ) : (
          <article className="surface-card">
            <div className="empty-state">
              No badges unlocked yet. A completed challenge or a milestone will unlock the first one.
            </div>
          </article>
        )}
      </section>
    </>
  )
}

export default DashboardPage
