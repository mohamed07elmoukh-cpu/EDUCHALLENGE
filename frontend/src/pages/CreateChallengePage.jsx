import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { createChallenge } from '../services/challenges'

const difficultyOptions = ['EASY', 'MEDIUM', 'HARD']

function mapBackendFieldErrors(fieldErrors = {}) {
  const mappedErrors = {}

  Object.entries(fieldErrors).forEach(([field, message]) => {
    if (field === 'title' || field === 'description' || field === 'category' || field === 'difficulty' || field === 'pointsReward' || field === 'questions') {
      mappedErrors[field] = message
      return
    }

    const questionMatch = field.match(/^questions\[(\d+)\]\.(questionText|points)$/)
    if (questionMatch) {
      const [, questionIndex, property] = questionMatch
      mappedErrors[property === 'questionText' ? `question-${questionIndex}` : `question-points-${questionIndex}`] = message
      return
    }

    const optionMatch = field.match(/^questions\[(\d+)\]\.options\[(\d+)\]\.(optionText|isCorrect)$/)
    if (optionMatch) {
      const [, questionIndex, optionIndex, property] = optionMatch

      if (property === 'optionText') {
        mappedErrors[`option-${questionIndex}-${optionIndex}`] = message
      } else {
        mappedErrors[`question-options-${questionIndex}`] = message
      }
      return
    }

    const optionsListMatch = field.match(/^questions\[(\d+)\]\.options$/)
    if (optionsListMatch) {
      mappedErrors[`question-options-${optionsListMatch[1]}`] = message
    }
  })

  return mappedErrors
}

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

function CreateChallengePage() {
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth()
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    difficulty: 'MEDIUM',
    pointsReward: 150,
    questions: [createEmptyQuestion()],
  })
  const [errors, setErrors] = useState({})
  const [serverMessage, setServerMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const pageMessage = useMemo(() => {
    if (isAuthenticated && user) {
      return `The QCM challenge will be created under ${user.username} and published to all users.`
    }

    return 'Log in first to create a QCM challenge that will be visible to all users.'
  }, [isAuthenticated, user])

  const handleChange = (event) => {
    const { name, value } = event.target
    setFormData((current) => ({ ...current, [name]: value }))
    setErrors((current) => ({ ...current, [name]: undefined }))
    setServerMessage('')
  }

  const handleQuestionChange = (questionIndex, field, value) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) =>
        index === questionIndex ? { ...question, [field]: value } : question,
      ),
    }))
    setErrors((current) => ({
      ...current,
      [`question-${questionIndex}`]: undefined,
      [`question-points-${questionIndex}`]: undefined,
    }))
    setServerMessage('')
  }

  const handleOptionChange = (questionIndex, optionIndex, value) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) => {
        if (index !== questionIndex) {
          return question
        }

        return {
          ...question,
          options: question.options.map((option, currentOptionIndex) =>
            currentOptionIndex === optionIndex ? { ...option, optionText: value } : option,
          ),
        }
      }),
    }))
    setErrors((current) => ({
      ...current,
      [`option-${questionIndex}-${optionIndex}`]: undefined,
      [`question-options-${questionIndex}`]: undefined,
    }))
    setServerMessage('')
  }

  const setCorrectOption = (questionIndex, optionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.map((question, index) => {
        if (index !== questionIndex) {
          return question
        }

        return {
          ...question,
          options: question.options.map((option, currentOptionIndex) => ({
            ...option,
            isCorrect: currentOptionIndex === optionIndex,
          })),
        }
      }),
    }))
    setErrors((current) => ({ ...current, [`question-options-${questionIndex}`]: undefined }))
    setServerMessage('')
  }

  const addQuestion = () => {
    setFormData((current) => ({
      ...current,
      questions: [...current.questions, createEmptyQuestion()],
    }))
    setServerMessage('')
  }

  const removeQuestion = (questionIndex) => {
    setFormData((current) => ({
      ...current,
      questions: current.questions.filter((_, index) => index !== questionIndex),
    }))
    setServerMessage('')
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
    setServerMessage('')
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
    setServerMessage('')
  }

  const validate = () => {
    const nextErrors = {}

    if (!isAuthenticated || !user) {
      nextErrors.form = 'You must be logged in to create a QCM challenge.'
    }

    if (!formData.title.trim()) {
      nextErrors.title = 'Title is required.'
    } else if (formData.title.trim().length < 5) {
      nextErrors.title = 'Title must contain at least 5 characters.'
    } else if (formData.title.trim().length > 150) {
      nextErrors.title = 'Title must not exceed 150 characters.'
    }

    if (!formData.description.trim()) {
      nextErrors.description = 'Description is required.'
    } else if (formData.description.trim().length < 20) {
      nextErrors.description = 'Description must contain at least 20 characters.'
    } else if (formData.description.trim().length > 3000) {
      nextErrors.description = 'Description must not exceed 3000 characters.'
    }

    if (!formData.category.trim()) {
      nextErrors.category = 'Category is required.'
    } else if (formData.category.trim().length > 80) {
      nextErrors.category = 'Category must not exceed 80 characters.'
    }

    if (Number(formData.pointsReward) < 10) {
      nextErrors.pointsReward = 'Reward must be at least 10 points.'
    } else if (Number(formData.pointsReward) > 1000) {
      nextErrors.pointsReward = 'Reward must not exceed 1000 points.'
    }

    if (formData.questions.length === 0) {
      nextErrors.questions = 'Add at least one question.'
    }

    formData.questions.forEach((question, questionIndex) => {
      if (!question.questionText.trim()) {
        nextErrors[`question-${questionIndex}`] = 'Question text is required.'
      } else if (question.questionText.trim().length > 500) {
        nextErrors[`question-${questionIndex}`] = 'Question text must not exceed 500 characters.'
      }

      if (Number(question.points) < 1) {
        nextErrors[`question-points-${questionIndex}`] = 'Question points must be at least 1.'
      } else if (Number(question.points) > 100) {
        nextErrors[`question-points-${questionIndex}`] = 'Question points must not exceed 100.'
      }

      if (question.options.length < 2) {
        nextErrors[`question-options-${questionIndex}`] = 'Each question needs at least 2 options.'
      }

      question.options.forEach((option, optionIndex) => {
        if (!option.optionText.trim()) {
          nextErrors[`option-${questionIndex}-${optionIndex}`] = 'Option text is required.'
        } else if (option.optionText.trim().length > 255) {
          nextErrors[`option-${questionIndex}-${optionIndex}`] = 'Option text must not exceed 255 characters.'
        }
      })

      const correctOptions = question.options.filter((option) => option.isCorrect).length

      if (correctOptions !== 1) {
        nextErrors[`question-options-${questionIndex}`] =
          'Each question must have exactly one correct answer.'
      }
    })

    return nextErrors
  }

  const buildPayload = () => ({
    title: formData.title.trim(),
    description: formData.description.trim(),
    category: formData.category.trim(),
    difficulty: formData.difficulty,
    pointsReward: Number(formData.pointsReward),
    visibility: 'PUBLIC',
    questions: formData.questions.map((question) => ({
      questionText: question.questionText.trim(),
      points: Number(question.points) || 1,
      options: question.options.map((option) => ({
        optionText: option.optionText.trim(),
        isCorrect: option.isCorrect,
      })),
    })),
  })

  const handleSubmit = async (event) => {
    event.preventDefault()
    const nextErrors = validate()
    setErrors(nextErrors)

    if (Object.keys(nextErrors).length > 0) {
      return
    }

    setIsSubmitting(true)
    setServerMessage('')

    try {
      const createdChallenge = await createChallenge(buildPayload(), user)

      navigate(`/challenges/${createdChallenge.id}`, {
        state: { creationSuccess: 'QCM challenge created successfully.' },
      })
    } catch (error) {
      const backendFieldErrors = mapBackendFieldErrors(error.fieldErrors)

      if (Object.keys(backendFieldErrors).length > 0) {
        setErrors((current) => ({ ...current, ...backendFieldErrors }))
        setServerMessage('Please correct the highlighted fields and try again.')
      } else {
        setServerMessage(error.message || 'Unable to create the QCM challenge.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="section">
      <div className="section-heading">
        <div>
          <span className="eyebrow">QCM Builder</span>
          <h1>Create a multiple-choice challenge</h1>
          <p>{pageMessage}</p>
        </div>
        <Link className="button-secondary" to="/challenges">
          Back to challenges
        </Link>
      </div>

      <article className="surface-card challenge-form-card">
        {serverMessage && <div className="error-banner">{serverMessage}</div>}
        {errors.form && <div className="error-banner">{errors.form}</div>}

        <form className="form-grid" onSubmit={handleSubmit} noValidate>
          <div className={`field ${errors.title ? 'has-error' : ''}`}>
            <label htmlFor="challenge-title">Title</label>
            <input
              className="form-input"
              id="challenge-title"
              name="title"
              type="text"
              value={formData.title}
              onChange={handleChange}
              placeholder="Example: Java Basics QCM"
            />
            {errors.title && <span className="field-error">{errors.title}</span>}
          </div>

          <div className={`field ${errors.description ? 'has-error' : ''}`}>
            <label htmlFor="challenge-description">Description</label>
            <textarea
              className="form-input form-textarea"
              id="challenge-description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Explain the learning goal, topic, and expected level."
            />
            {errors.description && <span className="field-error">{errors.description}</span>}
          </div>

          <div className="form-split challenge-qcm-meta">
            <div className={`field ${errors.category ? 'has-error' : ''}`}>
              <label htmlFor="challenge-category">Category</label>
              <input
                className="form-input"
                id="challenge-category"
                name="category"
                type="text"
                value={formData.category}
                onChange={handleChange}
                placeholder="Programming, Mathematics, Science..."
              />
              {errors.category && <span className="field-error">{errors.category}</span>}
            </div>

            <div className="field">
              <label htmlFor="challenge-difficulty">Difficulty</label>
              <select
                className="select-input"
                id="challenge-difficulty"
                name="difficulty"
                value={formData.difficulty}
                onChange={handleChange}
              >
                {difficultyOptions.map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))}
              </select>
            </div>

            <div className={`field ${errors.pointsReward ? 'has-error' : ''}`}>
              <label htmlFor="challenge-points">Points reward</label>
              <input
                className="form-input"
                id="challenge-points"
                name="pointsReward"
                type="number"
                min="10"
                max="1000"
                value={formData.pointsReward}
                onChange={handleChange}
              />
              {errors.pointsReward && <span className="field-error">{errors.pointsReward}</span>}
            </div>
          </div>

          <div className="success-banner">
            All newly created challenges are published as <strong>PUBLIC</strong>, so every user can see them in the Challenges section.
          </div>

          <div className={`field ${errors.questions ? 'has-error' : ''}`}>
            <label>Questions</label>
            <div className="questions-grid">
              {formData.questions.map((question, questionIndex) => (
                <article className="surface-card qcm-question-card" key={`question-${questionIndex}`}>
                  <div className="question-card-header">
                    <div>
                      <span className="question-number">Question {questionIndex + 1}</span>
                      <p className="muted-caption">Add the prompt, points, and answer options.</p>
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

                  <div className={`field ${errors[`question-${questionIndex}`] ? 'has-error' : ''}`}>
                    <label htmlFor={`question-text-${questionIndex}`}>Question text</label>
                    <textarea
                      className="form-input form-textarea"
                      id={`question-text-${questionIndex}`}
                      value={question.questionText}
                      onChange={(event) => handleQuestionChange(questionIndex, 'questionText', event.target.value)}
                      placeholder="Write the QCM question prompt here."
                    />
                    {errors[`question-${questionIndex}`] && (
                      <span className="field-error">{errors[`question-${questionIndex}`]}</span>
                    )}
                  </div>

                  <div className={`field question-points-field ${errors[`question-points-${questionIndex}`] ? 'has-error' : ''}`}>
                    <label htmlFor={`question-points-${questionIndex}`}>Points for this question</label>
                    <input
                      className="form-input"
                      id={`question-points-${questionIndex}`}
                      type="number"
                      min="1"
                      max="100"
                      value={question.points}
                      onChange={(event) => handleQuestionChange(questionIndex, 'points', event.target.value)}
                    />
                    {errors[`question-points-${questionIndex}`] && (
                      <span className="field-error">{errors[`question-points-${questionIndex}`]}</span>
                    )}
                  </div>

                  <div className={`field ${errors[`question-options-${questionIndex}`] ? 'has-error' : ''}`}>
                    <label>Options</label>
                    <div className="options-grid">
                      {question.options.map((option, optionIndex) => (
                        <div className="option-row" key={`option-${questionIndex}-${optionIndex}`}>
                          <label className="option-correct-toggle">
                            <input
                              className="option-selector"
                              type="radio"
                              name={`correct-option-${questionIndex}`}
                              checked={option.isCorrect}
                              onChange={() => setCorrectOption(questionIndex, optionIndex)}
                            />
                            <span>Correct</span>
                          </label>

                          <div className={`field option-input-field ${errors[`option-${questionIndex}-${optionIndex}`] ? 'has-error' : ''}`}>
                            <input
                              className="form-input"
                              type="text"
                              value={option.optionText}
                              onChange={(event) => handleOptionChange(questionIndex, optionIndex, event.target.value)}
                              placeholder={`Option ${optionIndex + 1}`}
                            />
                            {errors[`option-${questionIndex}-${optionIndex}`] && (
                              <span className="field-error">
                                {errors[`option-${questionIndex}-${optionIndex}`]}
                              </span>
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

                    {errors[`question-options-${questionIndex}`] && (
                      <span className="field-error">{errors[`question-options-${questionIndex}`]}</span>
                    )}

                    <button
                      className="button-secondary button-reset create-step-button"
                      type="button"
                      onClick={() => addOption(questionIndex)}
                    >
                      Add option
                    </button>
                  </div>
                </article>
              ))}
            </div>

            {errors.questions && <span className="field-error">{errors.questions}</span>}

            <button className="button-secondary button-reset create-step-button" type="button" onClick={addQuestion}>
              Add question
            </button>
          </div>

          <div className="details-actions">
            <button className="button-primary" type="submit" disabled={isSubmitting || !isAuthenticated}>
              {isSubmitting ? 'Creating...' : 'Create QCM challenge'}
            </button>
            <Link className="button-secondary" to="/dashboard">
              Back to dashboard
            </Link>
          </div>
        </form>
      </article>
    </section>
  )
}

export default CreateChallengePage
