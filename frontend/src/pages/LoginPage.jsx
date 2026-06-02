import { useMemo, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { ChartIcon, MedalIcon, RocketIcon, SparkIcon, TargetIcon, TrophyIcon } from '../components/Icons'
import { useAuth } from '../context/AuthContext'
import { loginUser } from '../services/auth'

const showcaseFeatures = [
  {
    icon: <RocketIcon />,
    title: 'Track your momentum',
    description: 'Fast access to recent challenges and your current streak.',
  },
  {
    icon: <ChartIcon />,
    title: 'See your progress instantly',
    description: 'Points, badges, and rank are waiting on your dashboard.',
  },
  {
    icon: <MedalIcon />,
    title: 'Unlock achievements',
    description: 'Earn badges as you climb the leaderboard.',
  },
]

const showcaseStats = [
  { value: '12.4k+', label: 'Learners' },
  { value: '340+', label: 'Challenges' },
  { value: '98%', label: 'Satisfaction' },
]

function GoogleMark() {
  return (
    <svg aria-hidden="true" viewBox="0 0 24 24">
      <path
        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
        fill="#4285F4"
      />
      <path
        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
        fill="#34A853"
      />
      <path
        d="M5.84 14.1c-.22-.66-.35-1.36-.35-2.1s.13-1.44.35-2.1V7.07H2.18A11 11 0 001 12c0 1.77.42 3.44 1.18 4.93l3.66-2.83z"
        fill="#FBBC05"
      />
      <path
        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15A11 11 0 0012 1C7.7 1 3.99 3.47 2.18 7.07l3.66 2.83c.87-2.6 3.3-4.52 6.16-4.52z"
        fill="#EA4335"
      />
    </svg>
  )
}

function GithubMark() {
  return (
    <svg aria-hidden="true" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 2C6.48 2 2 6.58 2 12.19c0 4.5 2.87 8.32 6.84 9.67.5.09.68-.22.68-.49l-.01-1.71c-2.78.61-3.37-1.36-3.37-1.36-.45-1.17-1.11-1.48-1.11-1.48-.91-.63.07-.62.07-.62 1 .07 1.53 1.05 1.53 1.05.89 1.55 2.34 1.1 2.91.84.09-.65.35-1.1.63-1.35-2.22-.26-4.56-1.13-4.56-5.02 0-1.11.39-2.01 1.03-2.72-.1-.26-.45-1.29.1-2.68 0 0 .84-.27 2.75 1.04A9.38 9.38 0 0112 7.07c.85.004 1.7.12 2.5.34 1.91-1.31 2.75-1.04 2.75-1.04.55 1.39.2 2.42.1 2.68.64.71 1.03 1.61 1.03 2.72 0 3.9-2.34 4.76-4.57 5.01.36.31.68.93.68 1.87l-.01 2.77c0 .27.18.59.69.49A10.22 10.22 0 0022 12.19C22 6.58 17.52 2 12 2z" />
    </svg>
  )
}

function MailIcon() {
  return (
    <svg aria-hidden="true" viewBox="0 0 20 20" fill="currentColor">
      <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
      <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
    </svg>
  )
}

function EyeIcon({ open }) {
  if (open) {
    return (
      <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" />
        <path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" />
        <path d="M14.12 14.12A3 3 0 019.88 9.88" />
        <line x1="1" y1="1" x2="23" y2="23" />
      </svg>
    )
  }

  return (
    <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  )
}

function LoginPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { login } = useAuth()
  const [formData, setFormData] = useState({
    email: location.state?.registeredEmail || '',
    password: '',
  })
  const [errors, setErrors] = useState({})
  const [serverMessage, setServerMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const emailLooksValid = useMemo(
    () => /\S+@\S+\.\S+/.test(formData.email.trim()),
    [formData.email],
  )

  const passwordLooksValid = formData.password.length >= 6

  const validate = () => {
    const nextErrors = {}

    if (!formData.email.trim()) {
      nextErrors.email = 'Email is required.'
    } else if (!emailLooksValid) {
      nextErrors.email = 'Enter a valid email address.'
    }

    if (!formData.password.trim()) {
      nextErrors.password = 'Password is required.'
    } else if (!passwordLooksValid) {
      nextErrors.password = 'Password must contain at least 6 characters.'
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
      const response = await loginUser({
        email: formData.email.trim(),
        password: formData.password,
      })

      if (response?.user) {
        login(response.user)
      }

      navigate('/dashboard', { replace: true })
    } catch (error) {
      if (error.fieldErrors && Object.keys(error.fieldErrors).length > 0) {
        setErrors((current) => ({ ...current, ...error.fieldErrors }))
      }
      setServerMessage(error.message || 'Unable to sign in right now.')
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

  const handleSocialClick = (provider) => {
    setServerMessage(`${provider} login is not connected yet. Use your EduChallenge email for now.`)
  }

  return (
    <section className="login-page-shell">
      <div className="login-stage">
        <div className="login-card">
          <article className="login-showcase">
            <div className="login-orb orb-one" />
            <div className="login-orb orb-two" />
            <div className="login-orb orb-three" />

            <div className="login-showcase-content">
              <span className="login-pill">
                <span className="login-pill-dot" />
                Welcome back
              </span>

              <div>
                <h1>Reconnect with your learning streak.</h1>
                <p className="login-showcase-copy">
                  Log in to continue challenges, review your ranking, and unlock the next badge.
                </p>
              </div>

              <div className="login-feature-list">
                {showcaseFeatures.map((feature) => (
                  <div className="login-feature" key={feature.title}>
                    <div className="login-feature-icon">{feature.icon}</div>
                    <div>
                      <strong>{feature.title}</strong>
                      <span>{feature.description}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="login-stats-bar">
              {showcaseStats.map((stat) => (
                <div className="login-stat" key={stat.label}>
                  <div className="login-stat-value">{stat.value}</div>
                  <div className="login-stat-label">{stat.label}</div>
                </div>
              ))}
            </div>
          </article>

          <article className="login-form-panel">
            <div className="login-form-intro">
              <span className="login-form-tag">Login</span>
              <h2>Sign in to EduChallenge</h2>
              <p>Access your personalized dashboard and keep your streak alive.</p>
            </div>

            <div className="login-socials">
              <button className="login-social-button" onClick={() => handleSocialClick('Google')} type="button">
                <GoogleMark />
                <span>Continue with Google</span>
              </button>
              <button className="login-social-button" onClick={() => handleSocialClick('GitHub')} type="button">
                <GithubMark />
                <span>Continue with GitHub</span>
              </button>
            </div>

            <div className="login-divider">
              <span>or with your email</span>
            </div>

            {location.state?.registrationSuccess && (
              <div className="success-banner">{location.state.registrationSuccess}</div>
            )}
            {serverMessage && <div className="error-banner">{serverMessage}</div>}

            <form className="form-grid" onSubmit={handleSubmit} noValidate>
              <div className={`field ${errors.email ? 'has-error' : ''}`}>
                <div className="field-header">
                  <label htmlFor="email">Email</label>
                </div>
                <div className="login-input-shell">
                  <input
                    className={`form-input login-input ${formData.email ? (emailLooksValid ? 'valid' : 'filled') : ''}`}
                    id="email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="you@example.com"
                  />
                  <span
                    className={`login-input-icon ${
                      formData.email ? (emailLooksValid ? 'ok' : 'show') : ''
                    }`}
                  >
                    <MailIcon />
                  </span>
                </div>
                {errors.email && <span className="field-error">{errors.email}</span>}
              </div>

              <div className={`field ${errors.password ? 'has-error' : ''}`}>
                <div className="field-header">
                  <label htmlFor="password">Password</label>
                  <button className="login-inline-link" type="button">
                    Forgot?
                  </button>
                </div>
                <div className="login-input-shell">
                  <input
                    className={`form-input login-input ${
                      formData.password ? (passwordLooksValid ? 'valid' : 'filled') : ''
                    }`}
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Enter your password"
                  />
                  <button
                    className="login-password-toggle"
                    onClick={() => setShowPassword((current) => !current)}
                    type="button"
                  >
                    <EyeIcon open={showPassword} />
                  </button>
                </div>
                {errors.password && <span className="field-error">{errors.password}</span>}
              </div>

              <button
                className={`login-submit-button ${isSubmitting ? 'is-submitting' : ''}`}
                disabled={isSubmitting}
                type="submit"
              >
                <span className="login-submit-inner">
                  <SparkIcon />
                  <span>{isSubmitting ? 'Signing in...' : 'Sign in'}</span>
                </span>
                <span className="login-submit-progress" />
              </button>
            </form>

            <div className="login-footer-links">
              <span>
                No account yet?{' '}
                <Link className="inline-link" to="/register">
                  Create one
                </Link>
              </span>
              <Link className="inline-link" to="/challenges">
                Browse challenges
              </Link>
            </div>

            <div className="login-mini-note">
              <TargetIcon />
              <span>Points, badges, and rank sync instantly after login.</span>
            </div>
          </article>
        </div>
      </div>
    </section>
  )
}

export default LoginPage
