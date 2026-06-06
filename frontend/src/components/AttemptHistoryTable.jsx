import { Link } from 'react-router-dom'

function formatAttemptDate(value) {
  if (!value) {
    return 'Not available'
  }

  try {
    return new Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    }).format(new Date(value))
  } catch {
    return value
  }
}

function formatAttemptStatus(value) {
  if (!value) {
    return 'Unknown'
  }

  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase()
}

function AttemptHistoryTable({ attempts }) {
  return (
    <div className="table-shell">
      <table className="leaderboard-table attempt-history-table">
        <thead>
          <tr>
            <th>Challenge name</th>
            <th>Score</th>
            <th>Date</th>
            <th>Status</th>
            <th>XP earned</th>
          </tr>
        </thead>
        <tbody>
          {attempts.map((attempt) => (
            <tr key={attempt.attemptId}>
              <td>
                <div className="attempt-history-name">
                  <Link className="attempt-history-link" to={`/challenges/${attempt.challengeId}`}>
                    {attempt.challengeTitle}
                  </Link>
                  {attempt.outcomeLabel && <span>{attempt.outcomeLabel}</span>}
                </div>
              </td>
              <td>{attempt.score}/{attempt.maxScore}</td>
              <td>{formatAttemptDate(attempt.completedAt)}</td>
              <td>
                <span className={`status-pill ${String(attempt.status || '').toLowerCase()}`}>
                  {formatAttemptStatus(attempt.status)}
                </span>
              </td>
              <td>
                <span className="tag">{attempt.xpEarned || 0} XP</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default AttemptHistoryTable
