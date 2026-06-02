import { Link } from 'react-router-dom'

function formatDate(value) {
  if (!value) {
    return 'Unknown date'
  }

  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}

function MyChallengeCard({ challenge, onDelete, deletingId }) {
  const difficultyClass = challenge.difficulty.toLowerCase()
  const statusClass = challenge.status.toLowerCase()
  const isDeleting = deletingId === challenge.id

  return (
    <article className="my-challenge-card">
      <div className="my-challenge-card-top">
        <div>
          <div className="meta-line" style={{ marginTop: 0, marginBottom: '0.6rem' }}>
            <span className={`pill ${difficultyClass}`}>{challenge.difficulty}</span>
            <span className={`status-pill ${statusClass}`}>{challenge.status}</span>
          </div>
          <h3>{challenge.title}</h3>
          <p>{challenge.description}</p>
        </div>
        <span className="tag">{challenge.pointsReward} pts</span>
      </div>

      <div className="meta-line">
        <span className="pill">{challenge.category}</span>
        <span className="pill">{challenge.questionsCount} questions</span>
        <span className="pill">{challenge.participantsCount} participants</span>
        <span className="pill">{challenge.attemptsCount} attempts</span>
      </div>

      <div className="my-challenge-card-footer">
        <span className="muted-caption">Created {formatDate(challenge.createdAt)}</span>

        <div className="my-challenge-actions">
          <Link className="button-secondary" to={`/challenges/${challenge.id}`}>
            View
          </Link>
          <Link
            className="button-secondary"
            to={`/challenges/${challenge.id}`}
            state={{ managementMode: 'edit' }}
          >
            Edit
          </Link>
          <button
            className="button-ghost button-reset"
            type="button"
            onClick={() => onDelete(challenge)}
            disabled={isDeleting}
          >
            {isDeleting ? 'Deleting...' : 'Delete'}
          </button>
          <Link
            className="button-secondary"
            to={`/challenges/${challenge.id}`}
            state={{ managementMode: 'results' }}
          >
            Results
          </Link>
        </div>
      </div>
    </article>
  )
}

export default MyChallengeCard
