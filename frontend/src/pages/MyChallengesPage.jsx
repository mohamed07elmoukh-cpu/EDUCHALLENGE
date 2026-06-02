import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { ChartIcon, RocketIcon, TargetIcon, UsersIcon } from '../components/Icons'
import MyChallengeCard from '../components/MyChallengeCard'
import StatCard from '../components/StatCard'
import { useAuth } from '../context/AuthContext'
import { deleteChallenge, fetchMyCreatedChallenges } from '../services/challenges'

const difficultyFilters = ['All difficulties', 'Easy', 'Medium', 'Hard']
const statusFilters = ['All statuses', 'ACTIVE', 'DRAFT', 'ARCHIVED']
const sortOptions = [
  { value: 'newest', label: 'Newest' },
  { value: 'oldest', label: 'Oldest' },
  { value: 'most-played', label: 'Most played' },
  { value: 'highest-reward', label: 'Highest reward' },
]

const statIcons = [
  <RocketIcon key="rocket" />,
  <TargetIcon key="target" />,
  <UsersIcon key="users" />,
  <ChartIcon key="chart" />,
]

function MyChallengesPage() {
  const { user, isAuthenticated } = useAuth()
  const [challengeItems, setChallengeItems] = useState([])
  const [loadingState, setLoadingState] = useState('idle')
  const [search, setSearch] = useState('')
  const [difficulty, setDifficulty] = useState(difficultyFilters[0])
  const [status, setStatus] = useState(statusFilters[0])
  const [sortBy, setSortBy] = useState(sortOptions[0].value)
  const [errorMessage, setErrorMessage] = useState('')
  const [deletingId, setDeletingId] = useState(null)

  useEffect(() => {
    let active = true

    async function loadMyChallenges() {
      if (!isAuthenticated || !user) {
        setChallengeItems([])
        setLoadingState('done')
        return
      }

      try {
        setLoadingState('loading')
        setErrorMessage('')
        const data = await fetchMyCreatedChallenges(user)

        if (active) {
          setChallengeItems(data)
          setLoadingState('done')
        }
      } catch (error) {
        if (active) {
          setLoadingState('error')
          setErrorMessage(error.message || 'Unable to load your created challenges.')
        }
      }
    }

    loadMyChallenges()

    return () => {
      active = false
    }
  }, [isAuthenticated, user])

  const filteredChallenges = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase()

    const filtered = challengeItems.filter((challenge) => {
      const matchesSearch =
        normalizedSearch === '' ||
        challenge.title.toLowerCase().includes(normalizedSearch) ||
        challenge.description.toLowerCase().includes(normalizedSearch)

      const matchesDifficulty =
        difficulty === 'All difficulties' || challenge.difficulty === difficulty

      const matchesStatus = status === 'All statuses' || challenge.status === status

      return matchesSearch && matchesDifficulty && matchesStatus
    })

    return [...filtered].sort((first, second) => {
      if (sortBy === 'oldest') {
        return new Date(first.createdAt).getTime() - new Date(second.createdAt).getTime()
      }

      if (sortBy === 'most-played') {
        return second.attemptsCount - first.attemptsCount
      }

      if (sortBy === 'highest-reward') {
        return second.pointsReward - first.pointsReward
      }

      return new Date(second.createdAt).getTime() - new Date(first.createdAt).getTime()
    })
  }, [challengeItems, search, difficulty, status, sortBy])

  const stats = useMemo(() => {
    const totalCreated = challengeItems.length
    const activeChallenges = challengeItems.filter((challenge) => challenge.status === 'ACTIVE').length
    const totalParticipants = challengeItems.reduce(
      (sum, challenge) => sum + challenge.participantsCount,
      0,
    )
    const totalAttempts = challengeItems.reduce((sum, challenge) => sum + challenge.attemptsCount, 0)

    return [
      {
        label: 'Total Created Challenges',
        value: totalCreated.toString(),
        hint: totalCreated === 0 ? 'No created challenges yet' : 'Challenges published by you',
      },
      {
        label: 'Active Challenges',
        value: activeChallenges.toString(),
        hint: activeChallenges === 0 ? 'No active challenges yet' : 'Currently visible and active',
      },
      {
        label: 'Total Participants',
        value: totalParticipants.toString(),
        hint: totalParticipants === 0 ? 'No participants yet' : 'Unique participants across your challenges',
      },
      {
        label: 'Total Attempts',
        value: totalAttempts.toString(),
        hint: totalAttempts === 0 ? 'No attempts yet' : 'Total challenge attempts recorded',
      },
    ]
  }, [challengeItems])

  const handleDelete = async (challenge) => {
    if (!user) {
      return
    }

    const confirmed = window.confirm(`Delete the challenge "${challenge.title}"?`)

    if (!confirmed) {
      return
    }

    try {
      setDeletingId(challenge.id)
      await deleteChallenge(challenge.id, user)
      setChallengeItems((current) => current.filter((item) => item.id !== challenge.id))
    } catch (error) {
      setErrorMessage(error.message || 'Unable to delete the selected challenge.')
      setLoadingState('error')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <>
      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">My Challenges</span>
            <h1>My Challenges</h1>
            <p>Manage, track and update the challenges you created.</p>
          </div>
          <Link className="button-primary" to="/challenges/create">
            + Create New Challenge
          </Link>
        </div>
      </section>

      {!isAuthenticated ? (
        <section className="section">
          <article className="surface-card empty-state">
            Log in to view and manage the challenges you created.
          </article>
        </section>
      ) : (
        <>
          <section className="section stat-grid">
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

          <section className="section">
            <article className="my-challenges-toolbar">
              <div className="field">
                <label htmlFor="my-challenges-search">Search by title</label>
                <input
                  className="form-input"
                  id="my-challenges-search"
                  type="text"
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                  placeholder="Search your challenges"
                />
              </div>

              <div className="field">
                <label htmlFor="my-challenges-difficulty">Difficulty</label>
                <select
                  className="select-input"
                  id="my-challenges-difficulty"
                  value={difficulty}
                  onChange={(event) => setDifficulty(event.target.value)}
                >
                  {difficultyFilters.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>

              <div className="field">
                <label htmlFor="my-challenges-status">Status</label>
                <select
                  className="select-input"
                  id="my-challenges-status"
                  value={status}
                  onChange={(event) => setStatus(event.target.value)}
                >
                  {statusFilters.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>

              <div className="field">
                <label htmlFor="my-challenges-sort">Sort by</label>
                <select
                  className="select-input"
                  id="my-challenges-sort"
                  value={sortBy}
                  onChange={(event) => setSortBy(event.target.value)}
                >
                  {sortOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            </article>
          </section>

          <section className="section">
            {loadingState === 'loading' ? (
              <div className="empty-state">Loading your created challenges...</div>
            ) : loadingState === 'error' ? (
              <div className="error-banner">{errorMessage || 'Unable to load your challenges.'}</div>
            ) : filteredChallenges.length === 0 ? (
              <div className="empty-state">
                {challengeItems.length === 0
                  ? 'You have not created any challenges yet. Start by publishing your first QCM.'
                  : 'No created challenge matches the current search, filters, or sort.'}
              </div>
            ) : (
              <div className="my-challenges-grid">
                {filteredChallenges.map((challenge) => (
                  <MyChallengeCard
                    key={challenge.id}
                    challenge={challenge}
                    onDelete={handleDelete}
                    deletingId={deletingId}
                  />
                ))}
              </div>
            )}
          </section>
        </>
      )}
    </>
  )
}

export default MyChallengesPage
