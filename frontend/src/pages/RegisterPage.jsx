import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { registerUser } from '../services/auth'

function RegisterPage() {
  const navigate = useNavigate()
  const { rememberRegisteredUser } = useAuth()
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [errors, setErrors] = useState({})
  const [serverMessage, setServerMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const validate = () => {
    const nextErrors = {}

    if (!formData.username.trim()) {
      nextErrors.username = 'Username is required.'
    }

    if (!formData.email.trim()) {
      nextErrors.email = 'Email is required.'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      nextErrors.email = 'Enter a valid email address.'
    }

    if (!formData.password.trim()) {
      nextErrors.password = 'Password is required.'
    } else if (formData.password.length < 6) {
      nextErrors.password = 'Password must contain at least 6 characters.'
    }

    if (!formData.confirmPassword.trim()) {
      nextErrors.confirmPassword = 'Please confirm your password.'
    } else if (formData.confirmPassword !== formData.password) {
      nextErrors.confirmPassword = 'Passwords do not match.'
    }

    return nextErrors
  }

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
      const response = await registerUser({
        username: formData.username.trim(),
        email: formData.email.trim(),
        password: formData.password,
      })

      if (response?.user) {
        rememberRegisteredUser(response.user)
      }

      navigate('/login', {
        replace: true,
        state: {
          registrationSuccess:
            response?.message || 'Inscription reussie. Vous pouvez maintenant vous connecter.',
          registeredEmail: formData.email.trim(),
        },
      })
    } catch (error) {
      if (error.fieldErrors && Object.keys(error.fieldErrors).length > 0) {
        setErrors((current) => ({ ...current, ...error.fieldErrors }))
      }
      setServerMessage(error.message || 'Une erreur est survenue pendant l inscription.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setFormData((current) => ({ ...current, [name]: value }))
    setServerMessage('')
    setErrors((current) => ({ ...current, [name]: undefined }))
  }

  return (
    <section className="auth-shell">
      <article className="auth-branding">
        <span className="eyebrow">Join the platform</span>
        <h2>Build your profile and start collecting points.</h2>
        <p>
          Create your account to enter challenges, compete with friends, and track every
          learning milestone in one place.
        </p>
        <ul className="stack-list">
          <li>
            <div>
              <strong>Challenge-based learning</strong>
              <p>Short, focused activities make progress feel immediate and rewarding.</p>
            </div>
          </li>
          <li>
            <div>
              <strong>Visible progress</strong>
              <p>Watch your rank, badges, and level rise as you keep learning.</p>
            </div>
          </li>
        </ul>
      </article>

      <article className="auth-card">
        <div>
          <span className="eyebrow">Register</span>
          <h1>Create your EduChallenge account</h1>
          <p className="form-note">
            Fill in your profile details and prepare your first learning streak.
          </p>
        </div>

        {serverMessage && <div className="error-banner">{serverMessage}</div>}

        <form className="form-grid" onSubmit={handleSubmit} noValidate>
          <div className={`field ${errors.username ? 'has-error' : ''}`}>
            <label htmlFor="username">Username</label>
            <input
              className="form-input"
              id="username"
              name="username"
              type="text"
              value={formData.username}
              onChange={handleChange}
              placeholder="Choose a unique username"
            />
            {errors.username && <span className="field-error">{errors.username}</span>}
          </div>

          <div className={`field ${errors.email ? 'has-error' : ''}`}>
            <label htmlFor="register-email">Email</label>
            <input
              className="form-input"
              id="register-email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="you@example.com"
            />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className={`field ${errors.password ? 'has-error' : ''}`}>
            <label htmlFor="register-password">Password</label>
            <input
              className="form-input"
              id="register-password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Create a password"
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <div className={`field ${errors.confirmPassword ? 'has-error' : ''}`}>
            <label htmlFor="confirm-password">Confirm password</label>
            <input
              className="form-input"
              id="confirm-password"
              name="confirmPassword"
              type="password"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Repeat your password"
            />
            {errors.confirmPassword && (
              <span className="field-error">{errors.confirmPassword}</span>
            )}
          </div>

          <button className="button-primary button-block" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Creating account...' : 'Register'}
          </button>
        </form>

        <div className="auth-links">
          <span>
            Already have an account?{' '}
            <Link to="/login" className="inline-link">
              Sign in
            </Link>
          </span>
          <Link to="/dashboard" className="inline-link">
            Preview dashboard
          </Link>
        </div>
      </article>
    </section>
  )
}

export default RegisterPage
