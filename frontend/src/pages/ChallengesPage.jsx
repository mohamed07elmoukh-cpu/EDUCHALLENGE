import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import ChallengeCard from '../components/ChallengeCard'
import SearchFilterBar from '../components/SearchFilterBar'
import { useAuth } from '../context/AuthContext'
import { fetchChallenges } from '../services/challenges'

const difficulties = ['All difficulties', 'Easy', 'Medium', 'Hard']

function ChallengesPage() {
  const { isAuthenticated, user } = useAuth()
  const [challengeItems, setChallengeItems] = useState([])
  const [search, setSearch] = useState('')
  const [difficulty, setDifficulty] = useState(difficulties[0])
  const [category, setCategory] = useState('All categories')
  const [loadingState, setLoadingState] = useState('idle')
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    let active = true

    async function loadChallenges() {
      if (!isAuthenticated || !user) {
        setChallengeItems([])
        setLoadingState('done')
        return
      }

      try {
        setLoadingState('loading')
        setErrorMessage('')
        const backendChallenges = await fetchChallenges(user)

        if (active) {
          setChallengeItems(backendChallenges)
        }
      } catch (error) {
        if (active) {
          setChallengeItems([])
          setLoadingState('error')
          setErrorMessage(error.message || 'Unable to load backend challenges.')
        }
      } finally {
        if (active) {
          setLoadingState('done')
        }
      }
    }

    loadChallenges()

    return () => {
      active = false
    }
  }, [isAuthenticated, user])

  const categories = useMemo(
    () => ['All categories', ...new Set(challengeItems.map((challenge) => challenge.category))],
    [challengeItems],
  )

  const filteredChallenges = useMemo(() => {
    return challengeItems.filter((challenge) => {
      const matchesSearch =
        challenge.title.toLowerCase().includes(search.toLowerCase()) ||
        challenge.description.toLowerCase().includes(search.toLowerCase()) ||
        challenge.category.toLowerCase().includes(search.toLowerCase())

      const matchesDifficulty =
        difficulty === 'All difficulties' || challenge.difficulty === difficulty

      const matchesCategory = category === 'All categories' || challenge.category === category

      return matchesSearch && matchesDifficulty && matchesCategory
    })
  }, [search, difficulty, category])

  return (
    <>
      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Challenge library</span>
            <h1>Discover your next mission</h1>
            <p>
              Browse learning challenges by topic, difficulty, and keyword. Les défis créés
              depuis le backend apparaissent ici automatiquement.
            </p>
          </div>
          {isAuthenticated && (
            <Link className="button-primary" to="/challenges/create">
              Créer un challenge
            </Link>
          )}
        </div>

        <SearchFilterBar
          search={search}
          onSearchChange={setSearch}
          difficulty={difficulty}
          onDifficultyChange={setDifficulty}
          category={category}
          onCategoryChange={setCategory}
          difficulties={difficulties}
          categories={categories}
        />

        {!isAuthenticated ? (
          <div className="empty-state">
            Log in to load the real public challenges from the backend.
          </div>
        ) : loadingState === 'loading' ? (
          <div className="empty-state">Chargement des challenges...</div>
        ) : loadingState === 'error' ? (
          <div className="error-banner">{errorMessage || 'Impossible de charger les challenges backend.'}</div>
        ) : filteredChallenges.length === 0 ? (
          <div className="empty-state">
            No real backend challenge matches the current filters.
          </div>
        ) : (
          <div className="challenge-grid">
            {filteredChallenges.map((challenge) => (
              <ChallengeCard key={challenge.id} challenge={challenge} />
            ))}
          </div>
        )}
      </section>
    </>
  )
}

export default ChallengesPage
