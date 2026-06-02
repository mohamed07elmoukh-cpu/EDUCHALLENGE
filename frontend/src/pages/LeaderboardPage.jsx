import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import LeaderboardTable from '../components/LeaderboardTable'
import { TrophyIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import { fetchLeaderboard } from '../services/gamification'

function LeaderboardPage() {
  const { user, isAuthenticated } = useAuth()
  const [reloadKey, setReloadKey] = useState(0)
  const [loadingState, setLoadingState] = useState(isAuthenticated ? 'loading' : 'idle')
  const [leaderboard, setLeaderboard] = useState({ entries: [], currentUserRank: null })
  const [errorMessage, setErrorMessage] = useState('')
  const userId = user?.id
  const userEmail = user?.email

  useEffect(() => {
    if (!isAuthenticated || !user) {
      setLoadingState('idle')
      setLeaderboard({ entries: [], currentUserRank: null })
      setErrorMessage('')
      return
    }

    let active = true

    async function loadLeaderboard() {
      setLoadingState('loading')
      setErrorMessage('')

      try {
        const response = await fetchLeaderboard(user)

        if (!active) {
          return
        }

        setLeaderboard(response)
        setLoadingState('done')
      } catch (error) {
        if (!active) {
          return
        }

        setLoadingState('error')
        setErrorMessage(error.message || 'Unable to load the leaderboard.')
      }
    }

    loadLeaderboard()

    return () => {
      active = false
    }
  }, [isAuthenticated, reloadKey, userEmail, userId])

  if (!isAuthenticated) {
    return (
      <section className="leaderboard-shell">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">
            <TrophyIcon />
            Ranking
          </span>
          <h1>Login to view the live leaderboard.</h1>
          <p>
            The leaderboard API also returns the connected user position, so the page is
            personalized after authentication.
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

  return (
    <section className="leaderboard-shell">
      <div className="section-heading">
        <div>
          <span className="eyebrow">
            <TrophyIcon />
            Ranking
          </span>
          <h1>Leaderboard</h1>
          <p>
            Track the top learners, compare points and levels, and see exactly where the
            connected user stands.
          </p>
        </div>
      </div>

      {leaderboard.currentUserRank && (
        <article className="surface-card rank-summary-card">
          <strong>Your current position: #{leaderboard.currentUserRank}</strong>
          <p className="muted-caption">Ranking is calculated from total points, level, and username.</p>
        </article>
      )}

      {loadingState === 'loading' && leaderboard.entries.length === 0 && (
        <article className="leaderboard-card" style={{ padding: '1.2rem' }}>
          <div className="empty-state">Loading leaderboard...</div>
        </article>
      )}

      {loadingState === 'error' && (
        <div className="error-banner">
          {errorMessage}
          <div className="details-actions" style={{ marginTop: '0.8rem' }}>
            <button className="button-secondary" onClick={() => setReloadKey((value) => value + 1)} type="button">
              Retry
            </button>
          </div>
        </div>
      )}

      {leaderboard.entries.length > 0 && (
        <article className="leaderboard-card" style={{ padding: '1.2rem' }}>
          <LeaderboardTable entries={leaderboard.entries} currentUserId={user?.id} />
        </article>
      )}
    </section>
  )
}

export default LeaderboardPage
