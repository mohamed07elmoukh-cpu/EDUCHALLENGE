import { useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeftIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import { fetchChallengeById, submitChallengeAttempt } from '../services/challenges'

function ChallengeDetailsPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { challengeId } = useParams()
  const { user, applyChallengeAttemptResult } = useAuth()
  const [challenge, setChallenge] = useState(null)
  const [loadingState, setLoadingState] = useState('loading')
  const [selectedAnswers, setSelectedAnswers] = useState({})
  const [activeQuestionId, setActiveQuestionId] = useState(null)
  const [submitState, setSubmitState] = useState('idle')
  const [submitError, setSubmitError] = useState('')
  const [attemptResult, setAttemptResult] = useState(null)

  useEffect(() => {
    let active = true

    async function loadChallenge() {
      try {
        const challengeData = await fetchChallengeById(challengeId, user)
        if (active) {
          setChallenge(challengeData)
          setLoadingState('done')
        }
      } catch {
        if (active) {
          setLoadingState('error')
        }
      }
    }

    loadChallenge()

    return () => {
      active = false
    }
  }, [challengeId, user])

  useEffect(() => {
    setSelectedAnswers({})
    setActiveQuestionId(null)
    setSubmitState('idle')
    setSubmitError('')
    setAttemptResult(null)
  }, [challengeId])

  const questions = Array.isArray(challenge?.questions) ? challenge.questions : []
  const hasQuestions = questions.length > 0
  const allQuestionsAnswered = hasQuestions && questions.every((question) => selectedAnswers[String(question.id)])
  const orderedQuestionIds = questions.map((question) => String(question.id))
  const answerResultsByStepId = useMemo(
    () => new Map((attemptResult?.answers || []).map((answer) => [String(answer.stepId), answer])),
    [attemptResult],
  )

  useEffect(() => {
    if (!hasQuestions) {
      setActiveQuestionId(null)
      return
    }

    setActiveQuestionId((current) => {
      if (current && orderedQuestionIds.includes(String(current))) {
        return String(current)
      }

      return orderedQuestionIds[0]
    })
  }, [hasQuestions, orderedQuestionIds])

  useEffect(() => {
    if (!hasQuestions || submitState === 'submitting' || attemptResult) {
      return undefined
    }

    function handleKeyboardSelection(event) {
      const pressedNumber = Number(event.key)

      if (event.key === 'Enter' && allQuestionsAnswered) {
        event.preventDefault()
        handleSubmitAttempt()
        return
      }

      if (!Number.isInteger(pressedNumber) || pressedNumber < 1 || pressedNumber > 9) {
        return
      }

      const targetQuestion = questions.find((question) => String(question.id) === String(activeQuestionId))

      if (!targetQuestion) {
        return
      }

      const targetOption = targetQuestion.options?.[pressedNumber - 1]

      if (!targetOption) {
        return
      }

      event.preventDefault()
      handleSelectAnswer(targetQuestion.id, targetOption.id)
    }

    window.addEventListener('keydown', handleKeyboardSelection)

    return () => {
      window.removeEventListener('keydown', handleKeyboardSelection)
    }
  }, [activeQuestionId, allQuestionsAnswered, attemptResult, hasQuestions, questions, submitState])

  async function handleSubmitAttempt() {
    if (!challenge || !hasQuestions) {
      return
    }

    if (!allQuestionsAnswered) {
      setSubmitError('Please choose one answer for every question before submitting the challenge.')
      return
    }

    setSubmitState('submitting')
    setSubmitError('')

    try {
      const result = await submitChallengeAttempt(
        challenge.id,
        questions.map((question) => ({
          stepId: question.id,
          selectedOptionId: Number(selectedAnswers[String(question.id)]),
        })),
        user,
      )

      setAttemptResult(result)
      setSubmitState('success')
      applyChallengeAttemptResult(result)
    } catch (error) {
      setSubmitState('error')
      setSubmitError(error.message || 'Unable to submit your answers right now.')
    }
  }

  function handleSelectAnswer(questionId, optionId) {
    if (submitState === 'submitting' || attemptResult) {
      return
    }

    setSelectedAnswers((current) => {
      const nextAnswers = {
        ...current,
        [String(questionId)]: String(optionId),
      }

      setActiveQuestionId(resolveNextQuestionId(questionId, nextAnswers))
      return nextAnswers
    })
    setSubmitError('')
  }

  function handleRetryAttempt() {
    setSelectedAnswers({})
    setActiveQuestionId(orderedQuestionIds[0] || null)
    setSubmitState('idle')
    setSubmitError('')
    setAttemptResult(null)
  }

  function resolveNextQuestionId(currentQuestionId, nextAnswers) {
    const currentIndex = orderedQuestionIds.findIndex((questionId) => questionId === String(currentQuestionId))
    const nextQuestionAfterCurrent = orderedQuestionIds
      .slice(currentIndex + 1)
      .find((questionId) => !nextAnswers[questionId])

    if (nextQuestionAfterCurrent) {
      return nextQuestionAfterCurrent
    }

    const firstUnansweredQuestion = orderedQuestionIds.find((questionId) => !nextAnswers[questionId])

    return firstUnansweredQuestion || String(currentQuestionId)
  }

  if (loadingState === 'loading' && !challenge) {
    return <section className="empty-state">Loading challenge...</section>
  }

  if (!challenge) {
    return (
      <section className="not-found">
        <div className="surface-card" style={{ maxWidth: '560px', textAlign: 'center' }}>
          <span className="eyebrow">Challenge not found</span>
          <h1>This challenge does not exist.</h1>
          <p className="muted-caption">
            The requested challenge could not be found or is not accessible with the current user.
          </p>
          <div className="details-actions" style={{ justifyContent: 'center' }}>
            <Link className="button-primary" to="/challenges">
              Back to challenges
            </Link>
          </div>
        </div>
      </section>
    )
  }

  return (
    <section className="details-grid">
      <article className="surface-card">
        {location.state?.creationSuccess && (
          <div className="success-banner" style={{ marginBottom: '1rem' }}>
            {location.state.creationSuccess}
          </div>
        )}
        {attemptResult && (
          <div
            className={`attempt-result-banner ${
              attemptResult.firstCompletion ? 'success-banner' : 'warning-banner'
            }`}
          >
            <strong>
              {attemptResult.correctAnswers}/{attemptResult.totalQuestions} correct answer(s)
            </strong>
            <p>
              {attemptResult.firstCompletion
                ? attemptResult.awardedPoints > 0
                  ? `Challenge completed. You earned ${attemptResult.awardedPoints} points.`
                  : 'Challenge completed. No reward points were earned this time.'
                : 'You already completed this challenge before. This new attempt was recorded without extra reward points.'}
            </p>
            <div className="attempt-result-grid">
              <span className="tag">Score {attemptResult.earnedScore}/{attemptResult.maxScore}</span>
              <span className="tag">Total points {attemptResult.totalPoints}</span>
              <span className="tag">Level {attemptResult.level}</span>
              <span className="tag">Streak {attemptResult.currentStreak} day(s)</span>
              {attemptResult.currentRank && <span className="tag">Rank #{attemptResult.currentRank}</span>}
            </div>
          </div>
        )}
        <span className={`pill ${challenge.difficulty.toLowerCase()}`}>{challenge.difficulty}</span>
        <h1 style={{ marginBottom: '0.75rem' }}>{challenge.title}</h1>
        <p>{challenge.description}</p>

        <div className="meta-line">
          <span className="tag">{challenge.pointsReward} points reward</span>
          <span className="pill">{challenge.category}</span>
          {challenge.visibility && <span className="pill">{challenge.visibility}</span>}
          {challenge.creatorUsername && <span className="pill">By {challenge.creatorUsername}</span>}
        </div>

        <div className="section-heading" style={{ marginTop: '2rem' }}>
          <div>
            <h3>{hasQuestions ? 'Questions' : 'Sample steps'}</h3>
            <p>
              {hasQuestions
                ? 'Select one answer for each question, then submit your challenge attempt.'
                : 'Preview what students will go through inside this challenge.'}
            </p>
          </div>
        </div>

        <div className="details-question-list">
          {hasQuestions ? (
            questions.map((question, index) => (
              <article
                className={[
                  'surface-card',
                  'details-question-card',
                  String(activeQuestionId) === String(question.id) ? 'active' : '',
                ]
                  .filter(Boolean)
                  .join(' ')}
                key={question.id || `question-${index}`}
                onClick={() => setActiveQuestionId(String(question.id))}
              >
                <div className="details-question-header">
                  <div>
                    <strong>Question {question.stepOrder || index + 1}</strong>
                    <p className="muted-caption">{question.points || 1} point(s)</p>
                  </div>
                  {!attemptResult && String(activeQuestionId) === String(question.id) && (
                    <span className="details-active-chip">Keyboard active</span>
                  )}
                </div>

                <p className="details-question-text">{question.questionText}</p>

                <div className="details-option-list">
                  {question.options?.map((option, optionIndex) => {
                    const answerResult = answerResultsByStepId.get(String(question.id))
                    const isSelected = selectedAnswers[String(question.id)] === String(option.id)
                    const isCorrectOption = answerResult
                      ? String(answerResult.correctOptionId) === String(option.id)
                      : false
                    const isWrongSelected = answerResult ? isSelected && !answerResult.isCorrect : false

                    return (
                      <button
                        className={[
                          'details-option-button',
                          isSelected ? 'selected' : '',
                          isCorrectOption ? 'correct' : '',
                          isWrongSelected ? 'incorrect' : '',
                        ]
                          .filter(Boolean)
                          .join(' ')}
                        key={option.id || option.optionText}
                        onClick={() => handleSelectAnswer(question.id, option.id)}
                        type="button"
                      >
                        <span className="details-option-marker">{String.fromCharCode(65 + optionIndex)}</span>
                        <span className="details-option-content">{option.optionText}</span>
                        {!answerResult && optionIndex < 9 && (
                          <span className="details-option-shortcut">PRESS {optionIndex + 1}</span>
                        )}
                        {answerResult && isCorrectOption && <span className="pill easy">Correct</span>}
                        {answerResult && isWrongSelected && <span className="pill hard">Your answer</span>}
                      </button>
                    )
                  })}
                </div>
              </article>
            ))
          ) : (
            <article className="surface-card" style={{ padding: '0 1.2rem' }}>
              {(challenge.steps || []).map((step, index) => (
                <div className="detail-step" key={step}>
                  <div>
                    <strong>Step {index + 1}</strong>
                    <p className="muted-caption">{step}</p>
                  </div>
                </div>
              ))}
            </article>
          )}
        </div>

        <div className="details-actions">
          {hasQuestions && !attemptResult && (
            <button
              className="button-primary"
              disabled={!allQuestionsAnswered || submitState === 'submitting'}
              onClick={handleSubmitAttempt}
              type="button"
            >
              {submitState === 'submitting' ? 'Submitting...' : 'Submit challenge'}
            </button>
          )}
          {attemptResult && (
            <button className="button-primary" onClick={handleRetryAttempt} type="button">
              Try again
            </button>
          )}
          <button className="button-secondary" type="button" onClick={() => navigate(-1)}>
            <ArrowLeftIcon />
            Back
          </button>
        </div>
        {submitError && <div className="error-banner">{submitError}</div>}
        {attemptResult?.unlockedBadges?.length > 0 && (
          <article className="surface-card attempt-feedback-card">
            <div className="section-heading">
              <div>
                <span className="eyebrow">Badges unlocked</span>
                <h3>New rewards earned</h3>
              </div>
            </div>
            <div className="badge-grid attempt-badge-grid">
              {attemptResult.unlockedBadges.map((badge) => (
                <div className="attempt-reward-badge" key={badge.id || badge.slug || badge.title}>
                  <strong>{badge.title}</strong>
                  <p className="muted-caption">{badge.description}</p>
                </div>
              ))}
            </div>
          </article>
        )}
        {attemptResult?.notifications?.length > 0 && (
          <article className="surface-card attempt-feedback-card">
            <div className="section-heading">
              <div>
                <span className="eyebrow">Recent achievements</span>
                <h3>Gamified notifications</h3>
              </div>
            </div>
            <div className="achievement-list">
              {attemptResult.notifications.map((notification) => (
                <div className="achievement-item" key={notification.id || notification.title}>
                  <div>
                    <strong>{notification.title}</strong>
                    <p className="muted-caption">{notification.message}</p>
                  </div>
                  <span className="achievement-type">{notification.notificationType}</span>
                </div>
              ))}
            </div>
          </article>
        )}
        {hasQuestions && !attemptResult && (
          <p className="challenge-play-hint">
            {Object.keys(selectedAnswers).length}/{questions.length} question(s) answered. Use keys 1-9 for the active question, then press Enter to submit.
          </p>
        )}
      </article>

      <article className="surface-card">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Challenge snapshot</span>
            <h2>What to expect</h2>
          </div>
        </div>
        <ul className="stack-list">
          <li>
            <div>
              <strong>Difficulty</strong>
              <p>{challenge.difficulty} level designed for gradual mastery.</p>
            </div>
          </li>
          <li>
            <div>
              <strong>Reward</strong>
              <p>Earn {challenge.pointsReward} points and improve your leaderboard position.</p>
            </div>
          </li>
          <li>
            <div>
              <strong>Questions</strong>
              <p>{hasQuestions ? `${questions.length} QCM question(s) in this challenge.` : 'No QCM questions yet.'}</p>
            </div>
          </li>
          <li>
            <div>
              <strong>Visibility</strong>
              <p>{challenge.visibility || 'PUBLIC'} challenge access.</p>
            </div>
          </li>
        </ul>
      </article>
    </section>
  )
}

export default ChallengeDetailsPage
