import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { ChartIcon, RocketIcon, SearchIcon, TargetIcon, TrophyIcon, UsersIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import {
  addAdminChallengeStep,
  createAdminChallenge,
  fetchAdminChallengeAttempts,
  fetchAdminChallengeDetails,
  fetchAdminChallengeLeaderboard,
  fetchAdminChallengeStats,
  fetchAdminChallenges,
} from '../services/adminChallenges'

const difficultyOptions = ['EASY', 'MEDIUM', 'HARD']
const adminDifficultyFilters = ['ALL', ...difficultyOptions]
const adminStatusFilters = ['ALL', 'ACTIVE', 'INACTIVE']
const adminSortOptions = [
  { value: 'newest', label: 'Newest' },
  { value: 'most-attempted', label: 'Most attempted' },
  { value: 'highest-score', label: 'Highest score' },
]

function createEmptyOption(isCorrect = false) {
  return {
    optionText: '',
    isCorrect,
  }
}

function createEmptyQuestion() {
  return {
    questionText: '',
    points: 1,
    options: [createEmptyOption(true), createEmptyOption(false)],
  }
}

function formatDateTime(value) {
  if (!value) {
    return 'Not completed yet'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString()
}

function formatDuration(seconds) {
  if (seconds == null || !Number.isFinite(Number(seconds))) {
    return 'N/A'
  }

  const totalSeconds = Math.max(0, Math.round(Number(seconds)))
  const minutes = Math.floor(totalSeconds / 60)
  const remainingSeconds = totalSeconds % 60

  if (minutes === 0) {
    return `${remainingSeconds}s`
  }

  return `${minutes}m ${remainingSeconds}s`
}

function AdminChallengesPage() {
  const { user, isAuthenticated, isAdmin } = useAuth()
  const [challenges, setChallenges] = useState([])
  const [selectedChallengeId, setSelectedChallengeId] = useState(null)
  const [selectedChallenge, setSelectedChallenge] = useState(null)
  const [challengeStats, setChallengeStats] = useState(null)
  const [challengeAttempts, setChallengeAttempts] = useState([])
  const [challengeLeaderboard, setChallengeLeaderboard] = useState([])
  const [listState, setListState] = useState('idle')
  const [detailsState, setDetailsState] = useState('idle')
  const [errorMessage, setErrorMessage] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const [isCreating, setIsCreating] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('ALL')
  const [difficultyFilter, setDifficultyFilter] = useState('ALL')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [sortBy, setSortBy] = useState('newest')
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    difficulty: 'MEDIUM',
    pointsReward: 150,
    questions: [createEmptyQuestion()],
  })
  const [formErrors, setFormErrors] = useState({})

  const summary = useMemo(() => {
    const totalChallenges = challenges.length
    const totalAttempts = challenges.reduce((sum, challenge) => sum + Number(challenge.totalAttempts || 0), 0)
    const totalParticipants = challenges.reduce(
      (sum, challenge) => sum + Number(challenge.uniqueParticipants || 0),
      0,
    )
    const activeChallenges = challenges.filter((challenge) => challenge.isActive).length

    return { totalChallenges, totalAttempts, totalParticipants, activeChallenges }
  }, [challenges])

  const categoryFilters = useMemo(() => {
    const uniqueCategories = Array.from(
      new Set(
        challenges
          .map((challenge) => challenge.category)
          .filter((category) => typeof category === 'string' && category.trim().length > 0),
      ),
    ).sort((left, right) => left.localeCompare(right))

    return ['ALL', ...uniqueCategories]
  }, [challenges])

  const filteredChallenges = useMemo(() => {
    const normalizedSearchTerm = searchTerm.trim().toLowerCase()

    const nextChallenges = challenges.filter((challenge) => {
      const titleMatches =
        normalizedSearchTerm.length === 0 ||
        String(challenge.title || '')
          .toLowerCase()
          .includes(normalizedSearchTerm)

      const categoryMatches = categoryFilter === 'ALL' || challenge.category === categoryFilter
      const difficultyMatches = difficultyFilter === 'ALL' || challenge.difficulty === difficultyFilter
      const statusMatches =
        statusFilter === 'ALL' ||
        (statusFilter === 'ACTIVE' ? Boolean(challenge.isActive) : !challenge.isActive)

      return titleMatches && categoryMatches && difficultyMatches && statusMatches
    })

    nextChallenges.sort((left, right) => {
      if (sortBy === 'most-attempted') {
        return (
          Number(right.totalAttempts || 0) - Number(left.totalAttempts || 0) ||
          new Date(right.createdAt || 0).getTime() - new Date(left.createdAt || 0).getTime()
        )
      }

      if (sortBy === 'highest-score') {
        return (
          Number(right.averageScore || 0) - Number(left.averageScore || 0) ||
          Number(right.totalAttempts || 0) - Number(left.totalAttempts || 0)
        )
      }

      return new Date(right.createdAt || 0).getTime() - new Date(left.createdAt || 0).getTime()
    })

    return nextChallenges
  }, [categoryFilter, challenges, difficultyFilter, searchTerm, sortBy, statusFilter])

  const hasActiveFilters = useMemo(
    () =>
      searchTerm.trim().length > 0 ||
      categoryFilter !== 'ALL' ||
      difficultyFilter !== 'ALL' ||
      statusFilter !== 'ALL' ||
      sortBy !== 'newest',
    [categoryFilter, difficultyFilter, searchTerm, sortBy, statusFilter],
  )

  useEffect(() => {
    if (!isAuthenticated || !isAdmin || !user) {
      setChallenges([])
      setSelectedChallengeId(null)
      setSelectedChallenge(null)
      return
    }

    let active = true

    async function loadChallenges() {
      setListState('loading')
      setErrorMessage('')

      try {
        const data = await fetchAdminChallenges(user)

        if (!active) {
          return
        }

        setChallenges(data)
        setSelectedChallengeId((current) => {
          if (current && data.some((challenge) => challenge.id === current)) {
            return current
          }

          return data[0]?.id ?? null
        })
        setListState('done')
      } catch (error) {
        if (!active) {
          return
        }

        setErrorMessage(error.message || 'Unable to load admin challenges.')
        setListState('error')
      }
    }

    loadChallenges()

    return () => {
      active = false
    }
  }, [isAdmin, isAuthenticated, user])

  useEffect(() => {
    if (!selectedChallengeId || !user || !isAdmin) {
      setSelectedChallenge(null)
      setChallengeStats(null)
      setChallengeAttempts([])
      setChallengeLeaderboard([])
      return
    }

    let active = true

    async function loadChallengeData() {
      setDetailsState('loading')
      setErrorMessage('')

      try {
        const [details, stats, attempts, leaderboard] = await Promise.all([
          fetchAdminChallengeDetails(selectedChallengeId, user),
          fetchAdminChallengeStats(selectedChallengeId, user),
          fetchAdminChallengeAttempts(selectedChallengeId, user),
          fetchAdminChallengeLeaderboard(selectedChallengeId, user),
        ])

        if (!active) {
          return
        }

        setSelectedChallenge(details)
        setChallengeStats(stats)
        setChallengeAttempts(attempts)
        setChallengeLeaderboard(leaderboard)
        setDetailsState('done')
      } catch (error) {
        if (!active) {
          return
        }

        setErrorMessage(error.message || 'Unable to load challenge analytics.')
        setDetailsState('error')
      }
    }

    loadChallengeData()

    return () => {
      active = false
    }
  }, [isAdmin, selectedChallengeId, user])

  useEffect(() => {
    if (!isAuthenticated || !isAdmin) {
      return
    }

    if (filteredChallenges.length === 0) {
      setSelectedChallengeId(null)
      return
    }

    setSelectedChallengeId((current) => {
      if (current && filteredChallenges.some((challenge) => challenge.id === current)) {
        return current
      }

      return filteredChallenges[0].id
    })
  }, [filteredChallenges, isAdmin, isAuthenticated])

  const handleMetaChange = (event) => {
    const { name, value } = event.target
    setFormData((current) => ({ ...current, [name]: value }))
    setFormErrors((current) => ({ ...current, [name]: undefined }))
    setErrorMessage('')
    setSuccessMessage('')
  }

  const handleQuestionChange = (questionIndex, field, value) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) =>
        index === questionIndex ? { ...question, [field]: value } : question,
      ),
    }))
    setErrorMessage('')
    setSuccessMessage('')
  }

  const handleOptionChange = (questionIndex, optionIndex, value) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) =>
        index === questionIndex
          ? {
              ...question,
              options: question.options.map((option, currentOptionIndex) =>
                currentOptionIndex === optionIndex ? { ...option, optionText: value } : option,
              ),
            }
          : question,
      ),
    }))
    setErrorMessage('')
    setSuccessMessage('')
  }

  const setCorrectOption = (questionIndex, optionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) =>
        index === questionIndex
          ? {
              ...question,
              options: question.options.map((option, currentOptionIndex) => ({
                ...option,
                isCorrect: currentOptionIndex === optionIndex,
              })),
            }
          : question,
      ),
    }))
    setErrorMessage('')
    setSuccessMessage('')
  }

  const addQuestion = () => {
    setFormData((current) => ({
      ...current,
      questions: [...current.questions, createEmptyQuestion()],
    }))
  }

  const removeQuestion = (questionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.filter((_, index) => index !== questionIndex),
    }))
  }

  const addOption = (questionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) =>
        index === questionIndex
          ? { ...question, options: [...question.options, createEmptyOption(false)] }
          : question,
      ),
    }))
  }

  const removeOption = (questionIndex, optionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) => {
        if (index !== questionIndex) {
          return question
        }

        const nextOptions = question.options.filter((_, currentOptionIndex) => currentOptionIndex !== optionIndex)
        const hasCorrectOption = nextOptions.some((option) => option.isCorrect)

        return {
          ...question,
          options: nextOptions.map((option, currentOptionIndex) => ({
            ...option,
            isCorrect: hasCorrectOption ? option.isCorrect : currentOptionIndex === 0,
          })),
        }
      }),
    }))
  }

  const validateForm = () => {
    const nextErrors = {}

    if (!formData.title.trim()) {
      nextErrors.title = 'Title is required.'
    }

    if (!formData.description.trim()) {
      nextErrors.description = 'Description is required.'
    }

    if (!formData.category.trim()) {
      nextErrors.category = 'Category is required.'
    }

    if (Number(formData.pointsReward) < 10) {
      nextErrors.pointsReward = 'Reward must be at least 10 points.'
    }

    if (formData.questions.length === 0) {
      nextErrors.questions = 'Add at least one question.'
    }

    formData.questions.forEach((question, questionIndex) => {
      if (!question.questionText.trim()) {
        nextErrors[`question-${questionIndex}`] = 'Question text is required.'
      }

      if (Number(question.points) < 1) {
        nextErrors[`question-points-${questionIndex}`] = 'Points must be at least 1.'
      }

      if (question.options.length < 2) {
        nextErrors[`question-options-${questionIndex}`] = 'Each question needs at least 2 options.'
      }

      const correctOptions = question.options.filter((option) => option.isCorrect).length
      if (correctOptions !== 1) {
        nextErrors[`question-options-${questionIndex}`] = 'Select exactly one correct option.'
      }

      question.options.forEach((option, optionIndex) => {
        if (!option.optionText.trim()) {
          nextErrors[`option-${questionIndex}-${optionIndex}`] = 'Option text is required.'
        }
      })
    })

    return nextErrors
  }

  const resetForm = () => {
    setFormData({
      title: '',
      description: '',
      category: '',
      difficulty: 'MEDIUM',
      pointsReward: 150,
      questions: [createEmptyQuestion()],
    })
    setFormErrors({})
  }

  const resetFilters = () => {
    setSearchTerm('')
    setCategoryFilter('ALL')
    setDifficultyFilter('ALL')
    setStatusFilter('ALL')
    setSortBy('newest')
  }

  const refreshAll = async (preferredChallengeId = null) => {
    const refreshedChallenges = await fetchAdminChallenges(user)
    setChallenges(refreshedChallenges)
    const nextSelectedId =
      preferredChallengeId ??
      (selectedChallengeId && refreshedChallenges.some((challenge) => challenge.id === selectedChallengeId)
        ? selectedChallengeId
        : refreshedChallenges[0]?.id ?? null)
    setSelectedChallengeId(nextSelectedId)
  }

  const handleCreateChallenge = async (event) => {
    event.preventDefault()

    const nextErrors = validateForm()
    setFormErrors(nextErrors)

    if (Object.keys(nextErrors).length > 0) {
      return
    }

    setIsCreating(true)
    setErrorMessage('')
    setSuccessMessage('')

    try {
      const createdChallenge = await createAdminChallenge(
        {
          title: formData.title.trim(),
          description: formData.description.trim(),
          category: formData.category.trim(),
          difficulty: formData.difficulty,
          pointsReward: Number(formData.pointsReward),
          visibility: 'PUBLIC',
          isActive: true,
        },
        user,
      )

      for (let index = 0; index < formData.questions.length; index += 1) {
        const question = formData.questions[index]

        await addAdminChallengeStep(
          createdChallenge.id,
          {
            questionText: question.questionText.trim(),
            stepOrder: index + 1,
            points: Number(question.points) || 1,
            options: question.options.map((option) => ({
              optionText: option.optionText.trim(),
              isCorrect: option.isCorrect,
            })),
          },
          user,
        )
      }

      await refreshAll(createdChallenge.id)
      resetForm()
      setSuccessMessage('Admin challenge created successfully and added to the management board.')
    } catch (error) {
      setErrorMessage(error.message || 'Unable to create the admin challenge.')
    } finally {
      setIsCreating(false)
    }
  }

  if (!isAuthenticated) {
    return (
      <section className="admin-page-shell">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">
            <RocketIcon />
            Admin
          </span>
          <h1>Login as an admin to manage challenge operations.</h1>
          <p>The admin console is available only to authenticated users with the ADMIN role.</p>
          <div className="details-actions">
            <Link className="button-primary" to="/login">
              Login
            </Link>
          </div>
        </article>
      </section>
    )
  }

  if (!isAdmin) {
    return (
      <section className="admin-page-shell">
        <article className="surface-card sign-in-prompt">
          <span className="eyebrow">
            <TargetIcon />
            Restricted
          </span>
          <h1>This page is reserved for admin accounts.</h1>
          <p>Your current account is authenticated, but it does not have the ADMIN role.</p>
        </article>
      </section>
    )
  }

  return (
    <section className="admin-page-shell">
      <div className="section-heading">
        <div>
          <span className="eyebrow">
            <RocketIcon />
            Admin Console
          </span>
          <h1>Admin Challenge Management</h1>
          <p>Create QCM challenges, inspect challenge-level leaderboard data, and review who attempted each challenge with timing details.</p>
        </div>
      </div>

      {(errorMessage || successMessage) && (
        <div className={errorMessage ? 'error-banner' : 'success-banner'}>
          {errorMessage || successMessage}
        </div>
      )}

      <div className="admin-summary-grid">
        <article className="stat-card">
          <div className="stat-card-top">
            <span className="eyebrow">Challenges</span>
            <TrophyIcon />
          </div>
          <div className="stat-value">{summary.totalChallenges}</div>
          <p className="muted-caption">Managed challenges visible to admins.</p>
        </article>
        <article className="stat-card">
          <div className="stat-card-top">
            <span className="eyebrow">Attempts</span>
            <ChartIcon />
          </div>
          <div className="stat-value">{summary.totalAttempts}</div>
          <p className="muted-caption">All attempts recorded across managed challenges.</p>
        </article>
        <article className="stat-card">
          <div className="stat-card-top">
            <span className="eyebrow">Participants</span>
            <UsersIcon />
          </div>
          <div className="stat-value">{summary.totalParticipants}</div>
          <p className="muted-caption">Total participant entries across challenges.</p>
        </article>
        <article className="stat-card">
          <div className="stat-card-top">
            <span className="eyebrow">Active</span>
            <TargetIcon />
          </div>
          <div className="stat-value">{summary.activeChallenges}</div>
          <p className="muted-caption">Challenges currently active and visible.</p>
        </article>
      </div>

      <div className="admin-layout-grid">
        <article className="surface-card admin-builder-panel">
          <div className="admin-panel-header">
            <div>
              <span className="eyebrow">Builder</span>
              <h2>Create a new admin challenge</h2>
            </div>
          </div>

          <form className="form-grid" onSubmit={handleCreateChallenge} noValidate>
            <div className={`field ${formErrors.title ? 'has-error' : ''}`}>
              <label htmlFor="admin-title">Title</label>
              <input
                className="form-input"
                id="admin-title"
                name="title"
                type="text"
                value={formData.title}
                onChange={handleMetaChange}
                placeholder="Example: Admin Java Basics Challenge"
              />
              {formErrors.title && <span className="field-error">{formErrors.title}</span>}
            </div>

            <div className={`field ${formErrors.description ? 'has-error' : ''}`}>
              <label htmlFor="admin-description">Description</label>
              <textarea
                className="form-input form-textarea"
                id="admin-description"
                name="description"
                value={formData.description}
                onChange={handleMetaChange}
                placeholder="Describe the objective, topic, and expected knowledge level."
              />
              {formErrors.description && <span className="field-error">{formErrors.description}</span>}
            </div>

            <div className="form-split admin-form-split">
              <div className={`field ${formErrors.category ? 'has-error' : ''}`}>
                <label htmlFor="admin-category">Category</label>
                <input
                  className="form-input"
                  id="admin-category"
                  name="category"
                  type="text"
                  value={formData.category}
                  onChange={handleMetaChange}
                  placeholder="Programming, Math, Science..."
                />
                {formErrors.category && <span className="field-error">{formErrors.category}</span>}
              </div>

              <div className="field">
                <label htmlFor="admin-difficulty">Difficulty</label>
                <select
                  className="select-input"
                  id="admin-difficulty"
                  name="difficulty"
                  value={formData.difficulty}
                  onChange={handleMetaChange}
                >
                  {difficultyOptions.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>

              <div className={`field ${formErrors.pointsReward ? 'has-error' : ''}`}>
                <label htmlFor="admin-points">Reward</label>
                <input
                  className="form-input"
                  id="admin-points"
                  name="pointsReward"
                  type="number"
                  min="10"
                  max="1000"
                  value={formData.pointsReward}
                  onChange={handleMetaChange}
                />
                {formErrors.pointsReward && <span className="field-error">{formErrors.pointsReward}</span>}
              </div>
            </div>

            <div className="field">
              <label>Questions</label>
              <div className="admin-question-stack">
                {formData.questions.map((question, questionIndex) => (
                  <article className="admin-question-card" key={`admin-question-${questionIndex}`}>
                    <div className="question-card-header">
                      <div>
                        <strong>Question {questionIndex + 1}</strong>
                        <p className="muted-caption">Define the prompt, points, and options.</p>
                      </div>
                      {formData.questions.length > 1 && (
                        <button
                          className="button-ghost button-reset"
                          type="button"
                          onClick={() => removeQuestion(questionIndex)}
                        >
                          Remove
                        </button>
                      )}
                    </div>

                    <div className={`field ${formErrors[`question-${questionIndex}`] ? 'has-error' : ''}`}>
                      <label>Question text</label>
                      <textarea
                        className="form-input form-textarea"
                        value={question.questionText}
                        onChange={(event) => handleQuestionChange(questionIndex, 'questionText', event.target.value)}
                        placeholder="Write the challenge question."
                      />
                      {formErrors[`question-${questionIndex}`] && (
                        <span className="field-error">{formErrors[`question-${questionIndex}`]}</span>
                      )}
                    </div>

                    <div className={`field ${formErrors[`question-points-${questionIndex}`] ? 'has-error' : ''}`}>
                      <label>Question points</label>
                      <input
                        className="form-input"
                        type="number"
                        min="1"
                        max="100"
                        value={question.points}
                        onChange={(event) => handleQuestionChange(questionIndex, 'points', event.target.value)}
                      />
                      {formErrors[`question-points-${questionIndex}`] && (
                        <span className="field-error">{formErrors[`question-points-${questionIndex}`]}</span>
                      )}
                    </div>

                    <div className={`field ${formErrors[`question-options-${questionIndex}`] ? 'has-error' : ''}`}>
                      <label>Options</label>
                      <div className="options-grid">
                        {question.options.map((option, optionIndex) => (
                          <div className="option-row" key={`admin-option-${questionIndex}-${optionIndex}`}>
                            <label className="option-correct-toggle">
                              <input
                                className="option-selector"
                                type="radio"
                                name={`admin-correct-option-${questionIndex}`}
                                checked={option.isCorrect}
                                onChange={() => setCorrectOption(questionIndex, optionIndex)}
                              />
                              <span>Correct</span>
                            </label>

                            <div className={`field option-input-field ${formErrors[`option-${questionIndex}-${optionIndex}`] ? 'has-error' : ''}`}>
                              <input
                                className="form-input"
                                type="text"
                                value={option.optionText}
                                onChange={(event) => handleOptionChange(questionIndex, optionIndex, event.target.value)}
                                placeholder={`Option ${optionIndex + 1}`}
                              />
                              {formErrors[`option-${questionIndex}-${optionIndex}`] && (
                                <span className="field-error">{formErrors[`option-${questionIndex}-${optionIndex}`]}</span>
                              )}
                            </div>

                            {question.options.length > 2 && (
                              <button
                                className="button-ghost button-reset"
                                type="button"
                                onClick={() => removeOption(questionIndex, optionIndex)}
                              >
                                Remove
                              </button>
                            )}
                          </div>
                        ))}
                      </div>

                      {formErrors[`question-options-${questionIndex}`] && (
                        <span className="field-error">{formErrors[`question-options-${questionIndex}`]}</span>
                      )}

                      <button className="button-secondary button-reset" type="button" onClick={() => addOption(questionIndex)}>
                        Add option
                      </button>
                    </div>
                  </article>
                ))}
              </div>

              {formErrors.questions && <span className="field-error">{formErrors.questions}</span>}

              <button className="button-secondary button-reset" type="button" onClick={addQuestion}>
                Add question
              </button>
            </div>

            <div className="details-actions">
              <button className="button-primary" type="submit" disabled={isCreating}>
                {isCreating ? 'Publishing...' : 'Create admin challenge'}
              </button>
            </div>
          </form>
        </article>

        <div className="admin-management-stack">
          <article className="surface-card admin-challenge-list-panel">
            <div className="admin-panel-header">
              <div>
                <span className="eyebrow">Management</span>
                <h2>Challenges overview</h2>
              </div>
            </div>

            <div className="admin-filters-header">
              <p className="muted-caption admin-results-caption">
                Showing {filteredChallenges.length} of {challenges.length} challenges
              </p>
              {hasActiveFilters && (
                <button className="button-ghost button-reset" type="button" onClick={resetFilters}>
                  Reset filters
                </button>
              )}
            </div>

            <div className="filters-bar admin-filters-bar">
              <div className="input-wrap">
                <span className="input-icon">
                  <SearchIcon />
                </span>
                <input
                  className="search-input"
                  type="search"
                  placeholder="Search by title"
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                />
              </div>

              <select
                className="select-input"
                value={categoryFilter}
                onChange={(event) => setCategoryFilter(event.target.value)}
              >
                {categoryFilters.map((option) => (
                  <option key={option} value={option}>
                    {option === 'ALL' ? 'All categories' : option}
                  </option>
                ))}
              </select>

              <select
                className="select-input"
                value={difficultyFilter}
                onChange={(event) => setDifficultyFilter(event.target.value)}
              >
                {adminDifficultyFilters.map((option) => (
                  <option key={option} value={option}>
                    {option === 'ALL' ? 'All difficulties' : option}
                  </option>
                ))}
              </select>

              <select
                className="select-input"
                value={statusFilter}
                onChange={(event) => setStatusFilter(event.target.value)}
              >
                {adminStatusFilters.map((option) => (
                  <option key={option} value={option}>
                    {option === 'ALL' ? 'All statuses' : option}
                  </option>
                ))}
              </select>

              <select
                className="select-input"
                value={sortBy}
                onChange={(event) => setSortBy(event.target.value)}
              >
                {adminSortOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    Sort: {option.label}
                  </option>
                ))}
              </select>
            </div>

            {listState === 'loading' && <div className="empty-state">Loading admin challenges...</div>}
            {listState !== 'loading' && challenges.length === 0 && (
              <div className="empty-state">No challenges yet. Use the builder to create the first one.</div>
            )}
            {listState !== 'loading' && challenges.length > 0 && filteredChallenges.length === 0 && (
              <div className="empty-state">No challenges match the current search and filters.</div>
            )}

            <div className="admin-challenge-list">
              {filteredChallenges.map((challenge) => (
                <button
                  key={challenge.id}
                  className={`admin-challenge-list-item ${selectedChallengeId === challenge.id ? 'is-selected' : ''}`}
                  type="button"
                  onClick={() => setSelectedChallengeId(challenge.id)}
                >
                  <div className="admin-challenge-list-top">
                    <strong>{challenge.title}</strong>
                    <span className={`pill ${String(challenge.difficulty || '').toLowerCase()}`}>
                      {challenge.difficulty}
                    </span>
                  </div>
                  <p>{challenge.category}</p>
                  <div className="meta-line">
                    <span className="tag">{challenge.totalAttempts || 0} attempts</span>
                    <span className="tag">{challenge.uniqueParticipants || 0} participants</span>
                    <span className="tag">{challenge.isActive ? 'Active' : 'Inactive'}</span>
                  </div>
                </button>
              ))}
            </div>
          </article>

          <article className="surface-card admin-analytics-panel">
            <div className="admin-panel-header">
              <div>
                <span className="eyebrow">Analytics</span>
                <h2>{selectedChallenge?.title || 'Select a challenge'}</h2>
                <p className="muted-caption">
                  {selectedChallenge
                    ? `${selectedChallenge.category} • ${selectedChallenge.difficulty} • ${selectedChallenge.questions?.length || 0} steps`
                    : 'Choose a challenge from the list to inspect leaderboard and attempts.'}
                </p>
              </div>
            </div>

            {detailsState === 'loading' && selectedChallengeId && (
              <div className="empty-state">Loading challenge analytics...</div>
            )}

            {!selectedChallengeId && <div className="empty-state">Select a challenge to view admin analytics.</div>}

            {selectedChallenge && challengeStats && (
              <>
                <div className="admin-stats-grid">
                  <div className="admin-mini-stat">
                    <span>Total attempts</span>
                    <strong>{challengeStats.totalAttempts || 0}</strong>
                  </div>
                  <div className="admin-mini-stat">
                    <span>Unique participants</span>
                    <strong>{challengeStats.uniqueParticipants || 0}</strong>
                  </div>
                  <div className="admin-mini-stat">
                    <span>Average score</span>
                    <strong>{challengeStats.averageScore ?? 0}</strong>
                  </div>
                  <div className="admin-mini-stat">
                    <span>Completed runs</span>
                    <strong>{challengeStats.completionCount || 0}</strong>
                  </div>
                </div>

                <div className="admin-analytics-grid">
                  <section className="admin-leaderboard-panel">
                    <div className="admin-subpanel-header">
                      <h3>Challenge leaderboard</h3>
                      <span className="muted-caption">Best score, fastest completion, last completion</span>
                    </div>

                    {challengeLeaderboard.length === 0 ? (
                      <div className="empty-state compact">No participant leaderboard data yet.</div>
                    ) : (
                      <div className="table-shell">
                        <table className="leaderboard-table admin-table">
                          <thead>
                            <tr>
                              <th>Rank</th>
                              <th>Participant</th>
                              <th>Best score</th>
                              <th>Completed</th>
                              <th>Fastest</th>
                            </tr>
                          </thead>
                          <tbody>
                            {challengeLeaderboard.map((entry) => (
                              <tr key={`${entry.participantId}-${entry.rank}`}>
                                <td>
                                  <span className={`rank-pill ${entry.rank <= 3 ? 'top-rank' : ''}`}>
                                    {entry.rank}
                                  </span>
                                </td>
                                <td>
                                  <div className="admin-table-usercell">
                                    <strong>{entry.participantUsername}</strong>
                                    <span>{entry.participantEmail}</span>
                                  </div>
                                </td>
                                <td>{entry.bestScore}</td>
                                <td>{entry.completedAttempts}</td>
                                <td>{formatDuration(entry.fastestCompletionSeconds)}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </section>

                  <section className="admin-attempts-panel">
                    <div className="admin-subpanel-header">
                      <h3>Attempts timeline</h3>
                      <span className="muted-caption">See who played the challenge and how long it took.</span>
                    </div>

                    {challengeAttempts.length === 0 ? (
                      <div className="empty-state compact">No attempts recorded for this challenge yet.</div>
                    ) : (
                      <div className="admin-attempt-list">
                        {challengeAttempts.map((attempt) => (
                          <article className="admin-attempt-item" key={attempt.attemptId}>
                            <div className="admin-attempt-top">
                              <div>
                                <strong>{attempt.participantUsername}</strong>
                                <p>{attempt.participantEmail}</p>
                              </div>
                              <span className="tag">{attempt.score} pts</span>
                            </div>
                            <div className="meta-line">
                              <span className="pill">{attempt.status}</span>
                              <span className="pill">Started: {formatDateTime(attempt.startedAt)}</span>
                              <span className="pill">Completed: {formatDateTime(attempt.completedAt)}</span>
                              <span className="pill">Duration: {formatDuration(attempt.durationSeconds)}</span>
                            </div>
                          </article>
                        ))}
                      </div>
                    )}
                  </section>
                </div>
              </>
            )}
          </article>
        </div>
      </div>
    </section>
  )
}

export default AdminChallengesPage
