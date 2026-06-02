import { Link } from 'react-router-dom'
import LeaderboardTable from '../components/LeaderboardTable'
import {
  BookIcon,
  ChartIcon,
  MedalIcon,
  RocketIcon,
  SparkIcon,
  TargetIcon,
  TrophyIcon,
  UsersIcon,
} from '../components/Icons'
import { challenges, currentUser, features, leaderboard } from '../data/mockData'

const featureIcons = [<RocketIcon key="rocket" />, <UsersIcon key="users" />, <MedalIcon key="medal" />, <ChartIcon key="chart" />]

const missionLanes = [
  {
    title: 'Launch focused missions',
    description: 'Short challenges keep motivation high and make progress visible every day.',
    icon: <TargetIcon />,
  },
  {
    title: 'Compete without friction',
    description: 'Leaderboards, streaks, and rewards bring momentum to every learning session.',
    icon: <TrophyIcon />,
  },
  {
    title: 'Build a real learning rhythm',
    description: 'Track badges, recent activity, and next milestones in one clean experience.',
    icon: <SparkIcon />,
  },
]

const spotlightChallenges = challenges.slice(0, 3)

function HomePage() {
  return (
    <>
      <section className="home-hero-shell section">
        <div className="home-hero-backdrop home-hero-backdrop-one" />
        <div className="home-hero-backdrop home-hero-backdrop-two" />

        <article className="home-hero-main">
          <div className="home-hero-copy">
            <span className="eyebrow">
              <SparkIcon />
              Education with momentum
            </span>
            <h1>Learn like a player. Progress like a champion.</h1>
            <p>
              EduChallenge turns lessons into missions, transforms consistency into streaks,
              and makes every point, badge, and leaderboard jump feel rewarding.
            </p>

            <div className="hero-actions">
              <Link className="button-primary" to="/register">
                Start your journey
              </Link>
              <Link className="button-secondary" to="/challenges">
                Explore challenges
              </Link>
            </div>

            <div className="home-metric-row">
              <div className="home-metric-card">
                <strong>120+</strong>
                <span>Active challenges</span>
              </div>
              <div className="home-metric-card">
                <strong>32%</strong>
                <span>Average streak growth</span>
              </div>
              <div className="home-metric-card">
                <strong>4.8/5</strong>
                <span>Student satisfaction</span>
              </div>
            </div>
          </div>

          <div className="home-hero-visual">
            <div className="home-visual-card home-visual-primary">
              <div className="home-visual-header">
                <span className="eyebrow">
                  <BookIcon />
                  Student snapshot
                </span>
                <span className="tag">Live preview</span>
              </div>

              <div className="home-profile-block">
                <div className="home-profile-avatar">SB</div>
                <div>
                  <strong>{currentUser.name}</strong>
                  <p className="muted-caption">@{currentUser.username}</p>
                </div>
              </div>

              <div className="home-progress-panel">
                <div>
                  <span className="muted-caption">Current level</span>
                  <strong>{currentUser.level}</strong>
                </div>
                <div>
                  <span className="muted-caption">Total points</span>
                  <strong>{currentUser.points.toLocaleString()}</strong>
                </div>
                <div>
                  <span className="muted-caption">Weekly rank</span>
                  <strong>#{currentUser.rank}</strong>
                </div>
              </div>
            </div>

            <div className="home-floating-card home-floating-card-top">
              <div className="home-floating-icon">
                <RocketIcon />
              </div>
              <div>
                <strong>Weekly challenge streak</strong>
                <p>4 missions completed this week</p>
              </div>
            </div>

            <div className="home-floating-card home-floating-card-bottom">
              <div className="home-floating-icon">
                <MedalIcon />
              </div>
              <div>
                <strong>Next badge is close</strong>
                <p>175 more points to enter the top 3</p>
              </div>
            </div>
          </div>
        </article>
      </section>

      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Mission flow</span>
            <h2>A learning system designed to keep momentum visible</h2>
            <p>
              The interface is built around clarity, reward, and fast feedback so the student
              always knows what to do next.
            </p>
          </div>
        </div>

        <div className="home-lane-grid">
          {missionLanes.map((lane) => (
            <article className="home-lane-card" key={lane.title}>
              <div className="home-lane-icon">{lane.icon}</div>
              <h3>{lane.title}</h3>
              <p>{lane.description}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Platform features</span>
            <h2>Everything learners need in one gamified workspace</h2>
            <p>
              From challenge creation to reward systems, each module is made to feel clean,
              modern, and motivating.
            </p>
          </div>
        </div>

        <div className="home-feature-grid">
          {features.map((feature, index) => (
            <article className="home-feature-card" key={feature.title}>
              <span className="home-feature-icon">{featureIcons[index]}</span>
              <h3>{feature.title}</h3>
              <p>{feature.description}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="home-spotlight-shell section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Challenge spotlight</span>
            <h2>Discover high-energy missions students love to start with</h2>
            <p>
              A curated set of challenges that combines quick wins, visible rewards, and
              strong replay value.
            </p>
          </div>
          <Link className="button-secondary" to="/challenges">
            View all challenges
          </Link>
        </div>

        <div className="home-spotlight-grid">
          {spotlightChallenges.map((challenge) => (
            <article className="home-spotlight-card" key={challenge.id}>
              <div className="home-spotlight-top">
                <span className={`pill ${challenge.difficulty.toLowerCase()}`}>
                  {challenge.difficulty}
                </span>
                <span className="tag">{challenge.pointsReward} pts</span>
              </div>

              <h3>{challenge.title}</h3>
              <p>{challenge.description}</p>

              <div className="meta-line">
                <span className="pill">{challenge.category}</span>
                <span className="pill">{challenge.duration}</span>
                <span className="pill">{challenge.participants} learners</span>
              </div>

              <div className="home-spotlight-footer">
                <span className="home-status-pill">{challenge.status}</span>
                <Link className="inline-link" to="/challenges">
                  Start mission
                </Link>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">
              <TrophyIcon />
              Leaderboard preview
            </span>
            <h2>Top learners this week</h2>
            <p>
              A quick preview of the current ranking. Jump into the full leaderboard to
              compare points, levels, and badges.
            </p>
          </div>
          <Link className="button-secondary" to="/leaderboard">
            See full ranking
          </Link>
        </div>

        <article className="home-leaderboard-panel">
          <div className="home-leaderboard-summary">
            <div>
              <span className="eyebrow">Your target</span>
              <h3>Break into the weekly podium</h3>
              <p>
                Keep solving missions, extend your streak, and collect the next badge to
                move above the competition.
              </p>
            </div>
            <div className="home-summary-stats">
              <div>
                <strong>#{currentUser.rank}</strong>
                <span>Current rank</span>
              </div>
              <div>
                <strong>{currentUser.points.toLocaleString()}</strong>
                <span>Total points</span>
              </div>
            </div>
          </div>

          <LeaderboardTable entries={leaderboard.slice(0, 5)} currentUserId={currentUser.id} />
        </article>
      </section>
    </>
  )
}

export default HomePage
