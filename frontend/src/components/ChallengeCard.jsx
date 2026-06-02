import { Link } from 'react-router-dom'

function ChallengeCard({ challenge }) {
  const difficultyClass = challenge.difficulty.toLowerCase()

  return (
    <article className="challenge-card">
      <div className="challenge-card-top">
        <div>
          <span className={`pill ${difficultyClass}`}>{challenge.difficulty}</span>
          <h3>{challenge.title}</h3>
        </div>
        <span className="tag">{challenge.pointsReward} pts</span>
      </div>

      <p>{challenge.description}</p>

      <div className="meta-line">
        <span className="pill">{challenge.category}</span>
        <span className="pill">{challenge.duration}</span>
        <span className="pill">{challenge.participants} learners</span>
      </div>

      <div className="challenge-card-actions">
        <Link className="button-primary" to={`/challenges/${challenge.id}`}>
          View challenge
        </Link>
        <Link className="button-secondary" to={`/challenges/${challenge.id}`}>
          Start now
        </Link>
      </div>
    </article>
  )
}

export default ChallengeCard
