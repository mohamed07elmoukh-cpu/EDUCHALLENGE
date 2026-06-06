import { useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeftIcon, BookmarkIcon, CommentIcon, HeartIcon, ShareIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import {
  createChallengeComment,
  fetchChallengeById,
  shareChallenge,
  submitChallengeAttempt,
  toggleChallengeLike,
  toggleSavedChallenge,
} from '../services/challenges'

function formatCommentDate(value) {
  if (!value) {
    return 'Just now'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString()
}

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
  const [socialMessage, setSocialMessage] = useState('')
  const [socialError, setSocialError] = useState('')
  const [socialActionState, setSocialActionState] = useState('idle')
  const [commentDraft, setCommentDraft] = useState('')
  const [commentState, setCommentState] = useState('idle')
  const [commentError, setCommentError] = useState('')

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
    setSocialMessage('')
    setSocialError('')
    setSocialActionState('idle')
    setCommentDraft('')
    setCommentState('idle')
    setCommentError('')
  }, [challengeId])

  const questions = Array.isArray(challenge?.questions) ? challenge.questions : []
  const comments = Array.isArray(challenge?.comments) ? challenge.comments : []
  const social = challenge?.social || {
    likeCount: 0,
    saveCount: 0,
    shareCount: 0,
    commentCount: 0,
    likedByCurrentUser: false,
    savedByCurrentUser: false,
    completedByCurrentUser: false,
    canComment: false,
  }
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
      const refreshedChallenge = await fetchChallengeById(challenge.id, user)
      setChallenge(refreshedChallenge)
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
    setSocialMessage('')
  }

  async function handleToggleLike() {
    if (!challenge || socialActionState === 'submitting') {
      return
    }

    setSocialActionState('submitting')
    setSocialError('')
    setSocialMessage('')

    try {
      const nextSocial = await toggleChallengeLike(challenge.id, user)
      setChallenge((current) => (current ? { ...current, social: nextSocial } : current))
    } catch (error) {
      setSocialError(error.message || 'Unable to update this like right now.')
    } finally {
      setSocialActionState('idle')
    }
  }

  async function handleToggleSave() {
    if (!challenge || socialActionState === 'submitting') {
      return
    }

    setSocialActionState('submitting')
    setSocialError('')
    setSocialMessage('')

    try {
      const nextSocial = await toggleSavedChallenge(challenge.id, user)
      setChallenge((current) => (current ? { ...current, social: nextSocial } : current))
      setSocialMessage(nextSocial.savedByCurrentUser ? 'Challenge saved to your list.' : 'Challenge removed from your saved list.')
    } catch (error) {
      setSocialError(error.message || 'Unable to update this saved challenge right now.')
    } finally {
      setSocialActionState('idle')
    }
  }

  async function handleShareChallenge() {
    if (!challenge || socialActionState === 'submitting') {
      return
    }

    const shareUrl = `${window.location.origin}/challenges/${challenge.id}`
    setSocialActionState('submitting')
    setSocialError('')
    setSocialMessage('')

    try {
      const nextSocial = await shareChallenge(challenge.id, user)
      setChallenge((current) => (current ? { ...current, social: nextSocial } : current))

      if (navigator.share) {
        await navigator.share({
          title: challenge.title,
          text: `Try this EduChallenge challenge: ${challenge.title}`,
          url: shareUrl,
        })
      } else if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(shareUrl)
      }

      setSocialMessage('Challenge link ready to share.')
    } catch (error) {
      setSocialError(error.message || 'Unable to share this challenge right now.')
    } finally {
      setSocialActionState('idle')
    }
  }

  async function handlePublishComment() {
    if (!challenge || !social.canComment || commentState === 'submitting') {
      return
    }

    if (!commentDraft.trim()) {
      setCommentError('Write a short comment before publishing.')
      return
    }

    setCommentState('submitting')
    setCommentError('')

    try {
      const createdComment = await createChallengeComment(
        challenge.id,
        { content: commentDraft.trim() },
        user,
      )

      setChallenge((current) => {
        if (!current) {
          return current
        }

        return {
          ...current,
          comments: [createdComment, ...(Array.isArray(current.comments) ? current.comments : [])],
          social: current.social
            ? {
                ...current.social,
                commentCount: Number(current.social.commentCount || 0) + 1,
              }
            : current.social,
        }
      })
      setCommentDraft('')
      setCommentState('success')
    } catch (error) {
      setCommentState('error')
      setCommentError(error.message || 'Unable to publish your comment.')
    }
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

        <div className="challenge-social-shell">
          <div className="challenge-social-bar">
            <button
              className={`challenge-social-button ${social.likedByCurrentUser ? 'active' : ''}`}
              type="button"
              onClick={handleToggleLike}
              disabled={socialActionState === 'submitting'}
            >
              <HeartIcon />
              <span>Like</span>
              <strong>{social.likeCount || 0}</strong>
            </button>
            <button
              className={`challenge-social-button ${social.savedByCurrentUser ? 'active' : ''}`}
              type="button"
              onClick={handleToggleSave}
              disabled={socialActionState === 'submitting'}
            >
              <BookmarkIcon />
              <span>Save</span>
              <strong>{social.saveCount || 0}</strong>
            </button>
            <button
              className="challenge-social-button"
              type="button"
              onClick={handleShareChallenge}
              disabled={socialActionState === 'submitting'}
            >
              <ShareIcon />
              <span>Share</span>
              <strong>{social.shareCount || 0}</strong>
            </button>
            <div className="challenge-social-stats">
              <span className="pill">Comments {social.commentCount || 0}</span>
              {social.completedByCurrentUser && <span className="pill easy">Completed</span>}
            </div>
          </div>

          {socialMessage && <div className="success-banner">{socialMessage}</div>}
          {socialError && <div className="error-banner">{socialError}</div>}
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

        <div className="challenge-comments-shell">
          <div className="section-heading">
            <div>
              <span className="eyebrow">
                <CommentIcon />
                Discussion
              </span>
              <h3>Challenge comments</h3>
              <p>Light social feedback from learners who completed this challenge.</p>
            </div>
          </div>

          <div className="challenge-comment-form">
            <textarea
              className="form-input form-textarea"
              value={commentDraft}
              onChange={(event) => {
                setCommentDraft(event.target.value)
                setCommentError('')
              }}
              placeholder={
                social.canComment
                  ? 'Share what you learned, what was tricky, or what you enjoyed.'
                  : 'Complete this challenge first to unlock comments.'
              }
              disabled={!social.canComment || commentState === 'submitting'}
            />

            <div className="comment-form-footer">
              <p className="muted-caption">
                {social.canComment
                  ? 'Comments are reserved for users who completed the challenge.'
                  : 'You can comment after at least one completed attempt.'}
              </p>
              <button
                className="button-primary"
                type="button"
                onClick={handlePublishComment}
                disabled={!social.canComment || commentState === 'submitting'}
              >
                {commentState === 'submitting' ? 'Publishing...' : 'Post comment'}
              </button>
            </div>

            {commentError && <div className="error-banner">{commentError}</div>}
          </div>

          <div className="challenge-comments-list">
            {comments.length === 0 ? (
              <div className="empty-state compact">
                No comments yet. Finish the challenge and start the discussion.
              </div>
            ) : (
              comments.map((comment) => (
                <article className="challenge-comment-card" key={comment.id}>
                  <div className="challenge-comment-top">
                    <div>
                      <strong>{comment.authorUsername}</strong>
                      <p>{comment.authorEmail}</p>
                    </div>
                    <span className="pill">{formatCommentDate(comment.createdAt)}</span>
                  </div>
                  <p className="challenge-comment-content">{comment.content}</p>
                </article>
              ))
            )}
          </div>
        </div>
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
