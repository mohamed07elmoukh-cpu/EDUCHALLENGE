import { useMemo, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { CloseIcon, MenuIcon } from './Icons'

const guestLinks = [
  { to: '/', label: 'Home' },
  { to: '/challenges', label: 'Challenges' },
  { to: '/leaderboard', label: 'Leaderboard' },
]

const userLinks = [
  { to: '/', label: 'Home' },
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/challenges', label: 'Challenges' },
  { to: '/my-challenges', label: 'My Challenges' },
  { to: '/leaderboard', label: 'Leaderboard' },
  { to: '/profile', label: 'Profile' },
]

const adminLinks = [
  { to: '/', label: 'Home' },
  { to: '/challenges', label: 'Challenges' },
  { to: '/leaderboard', label: 'Leaderboard' },
  { to: '/admin/challenges', label: 'Admin Panel' },
  { to: '/profile', label: 'Profile' },
]

function Navbar() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const { isAuthenticated, isAdmin, logout } = useAuth()
  const links = useMemo(() => {
    if (!isAuthenticated) {
      return guestLinks
    }

    return isAdmin ? adminLinks : userLinks
  }, [isAdmin, isAuthenticated])

  const closeMenu = () => setOpen(false)
  const handleLogout = () => {
    logout()
    closeMenu()
    navigate('/login')
  }

  return (
    <header className="site-header">
      <div className="site-header-inner">
        <Link className="brand" to="/" onClick={closeMenu}>
          <span className="brand-mark">
            <img className="brand-logo" src="/logo.png" alt="EduChallenge logo" />
          </span>
          <span className="brand-text">
            <span className="brand-title">
              <span className="brand-title-edu">Edu</span>
              <span className="brand-title-challenge">Challenge</span>
            </span>
          </span>
        </Link>

        <nav className={`site-nav ${open ? 'is-open' : ''}`}>
          {links.map((link) => (
            <NavLink
              key={link.to}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              to={link.to}
              onClick={closeMenu}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className={`header-actions ${open ? 'is-open' : ''}`}>
          {isAuthenticated ? (
            <>
              <Link className="button-primary" to="/challenges/create" onClick={closeMenu}>
                Create challenge
              </Link>
              <button className="button-secondary button-reset" type="button" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link className="button-secondary" to="/login" onClick={closeMenu}>
                Login
              </Link>
              <Link className="button-primary" to="/register" onClick={closeMenu}>
                Join now
              </Link>
            </>
          )}
        </div>

        <button
          aria-expanded={open}
          aria-label={open ? 'Close navigation' : 'Open navigation'}
          className="nav-toggle"
          type="button"
          onClick={() => setOpen((value) => !value)}
        >
          {open ? <CloseIcon /> : <MenuIcon />}
        </button>
      </div>
    </header>
  )
}

export default Navbar
