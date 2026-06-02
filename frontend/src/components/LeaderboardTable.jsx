function LeaderboardTable({ entries, currentUserId }) {
  return (
    <div className="table-shell">
      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>User</th>
            <th>Points</th>
            <th>Level</th>
            <th>Badge</th>
          </tr>
        </thead>
        <tbody>
          {entries.map((entry) => {
            const isCurrentUser = entry.id === currentUserId

            return (
              <tr
                key={entry.id}
                className={`leaderboard-row ${isCurrentUser ? 'current-user' : ''}`}
              >
                <td>
                  <span className={`rank-pill ${entry.rank <= 3 ? 'top-rank' : ''}`}>
                    {entry.rank}
                  </span>
                </td>
                <td>{entry.username}</td>
                <td>{entry.points.toLocaleString()} pts</td>
                <td>
                  <span className="level-chip">{entry.level}</span>
                </td>
                <td>{entry.badge}</td>
              </tr>
            )
          })}
        </tbody>
      </table>

      <div className="mobile-leaderboard">
        {entries.map((entry) => (
          <div
            key={entry.id}
            className={`mobile-leaderboard-item ${entry.id === currentUserId ? 'current-user' : ''}`}
          >
            <div>
              <strong>{entry.username}</strong>
              <p className="muted-caption">
                {entry.points.toLocaleString()} pts | {entry.level}
              </p>
            </div>
            <span className={`rank-pill ${entry.rank <= 3 ? 'top-rank' : ''}`}>
              {entry.rank}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default LeaderboardTable
