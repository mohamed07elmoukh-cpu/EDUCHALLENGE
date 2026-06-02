import { Link } from 'react-router-dom'

function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer-inner">
        <div>
          <p className="footer-title">EduChallenge</p>
          <p className="footer-copy">
            A modern learning platform where students create challenges, earn rewards,
            and grow through healthy competition.
          </p>
        </div>

        <div>
          <p className="footer-title">Explore</p>
          <div className="footer-links">
            <Link className="footer-link" to="/dashboard">
              Dashboard
            </Link>
            <Link className="footer-link" to="/challenges">
              Challenges
            </Link>
            <Link className="footer-link" to="/leaderboard">
              Leaderboard
            </Link>
          </div>
        </div>

        <div>
          <p className="footer-title">Account</p>
          <div className="footer-links">
            <Link className="footer-link" to="/login">
              Login
            </Link>
            <Link className="footer-link" to="/register">
              Register
            </Link>
            <Link className="footer-link" to="/profile">
              Profile
            </Link>
          </div>
        </div>
      </div>
    </footer>
  )
}

export default Footer
