import { Link } from 'react-router-dom'
import { DiscordIcon, GitHubIcon, LinkedInIcon, TwitterXIcon } from './Icons'

const socialLinks = [
  { href: 'https://github.com', label: 'GitHub', icon: GitHubIcon },
  { href: 'https://www.linkedin.com', label: 'LinkedIn', icon: LinkedInIcon },
  { href: 'https://x.com', label: 'Twitter/X', icon: TwitterXIcon },
  { href: 'https://discord.com', label: 'Discord', icon: DiscordIcon },
]

const exploreLinks = [
  {
    to: '/dashboard',
    label: 'Dashboard',
    description: 'See your streak, badges, and current momentum at a glance.',
  },
  {
    to: '/challenges',
    label: 'Challenges',
    description: 'Browse public educational challenges and discover new topics.',
  },
  {
    to: '/leaderboard',
    label: 'Leaderboard',
    description: 'Compare scores and follow the most active learners on the platform.',
  },
]

const accountLinks = [
  {
    to: '/login',
    label: 'Login',
    description: 'Reconnect with your progress and continue your learning streak.',
  },
  {
    to: '/register',
    label: 'Register',
    description: 'Create a profile and start collecting points through challenge play.',
  },
  {
    to: '/profile',
    label: 'Profile',
    description: 'Manage your learner identity, stats, and recent achievements.',
  },
]

function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer-inner">
        <div className="footer-brand-column">
          <span className="footer-brand-eyebrow">Learn. Challenge. Rise.</span>

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

          <div className="footer-brand-highlights">
            <span className="footer-highlight-pill">Interactive QCM</span>
            <span className="footer-highlight-pill">Points and badges</span>
            <span className="footer-highlight-pill">Friendly competition</span>
          </div>

          <div className="footer-cta-inline">
            <div className="footer-cta-copy">
              <span className="footer-cta-badge">Start a new learning loop</span>
              <p className="footer-cta-title">Turn one topic into a challenge worth sharing.</p>
              <p className="footer-cta-text">
                Publish a challenge, invite learners to play, and build momentum through progress.
              </p>
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
          <div className="footer-link-cards">
            {exploreLinks.map((link) => (
              <Link className="footer-link-card" key={link.to} to={link.to}>
                <strong>{link.label}</strong>
                <span>{link.description}</span>
              </Link>
            ))}
          </div>
        </div>

        <div className="footer-column">
          <p className="footer-title">Account</p>
          <div className="footer-link-cards">
            {accountLinks.map((link) => (
              <Link className="footer-link-card" key={link.to} to={link.to}>
                <strong>{link.label}</strong>
                <span>{link.description}</span>
              </Link>
            ))}
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
