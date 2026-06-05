import { Link } from 'react-router-dom'
import { DiscordIcon, GitHubIcon, LinkedInIcon, TwitterXIcon } from './Icons'

const socialLinks = [
  { href: 'https://github.com', label: 'GitHub', icon: GitHubIcon },
  { href: 'https://www.linkedin.com', label: 'LinkedIn', icon: LinkedInIcon },
  { href: 'https://x.com', label: 'Twitter/X', icon: TwitterXIcon },
  { href: 'https://discord.com', label: 'Discord', icon: DiscordIcon },
]

function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer-inner">
        <div className="footer-brand-column">
          <Link className="footer-brand" to="/">
            <span className="footer-brand-mark">
              <img className="footer-brand-logo" src="/logo.png" alt="EduChallenge logo" />
            </span>
            <span className="footer-brand-text">
              <span className="footer-brand-title">
                <span className="footer-brand-title-edu">Edu</span>
                <span className="footer-brand-title-challenge">Challenge</span>
              </span>
            </span>
          </Link>

          <p className="footer-copy">
            A modern learning platform where students create challenges, earn rewards,
            and grow through healthy competition.
          </p>

          <div className="footer-cta-inline">
            <div className="footer-cta-copy">
              <p className="footer-cta-title">Ready to improve your skills?</p>
              <p className="footer-cta-text">Create a challenge and start learning today.</p>
            </div>
            <Link className="button-primary footer-cta-link" to="/challenges/create">
              Create challenge
            </Link>
          </div>

          <div className="footer-socials" aria-label="EduChallenge social links">
            {socialLinks.map(({ href, label, icon: Icon }) => (
              <a
                key={label}
                className="footer-social-link"
                href={href}
                target="_blank"
                rel="noreferrer"
                aria-label={label}
                title={label}
              >
                <Icon />
              </a>
            ))}
          </div>
        </div>

        <div className="footer-column">
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

        <div className="footer-column">
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

      <div className="site-footer-bottom">
        <div className="site-footer-bottom-inner">
          <p className="footer-copyright">&copy; 2026 EduChallenge. All rights reserved.</p>
          <div className="site-footer-meta">
            <p className="footer-copyright-note">Built for gamified learning.</p>
            <div className="footer-policy-links">
              <a className="footer-policy-link" href="#privacy">
                Privacy Policy
              </a>
              <span className="footer-policy-separator" aria-hidden="true">
                &middot;
              </span>
              <a className="footer-policy-link" href="#terms">
                Terms of Service
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  )
}

export default Footer
