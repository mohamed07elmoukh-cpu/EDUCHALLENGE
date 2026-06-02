function StatCard({ icon, label, value, hint }) {
  return (
    <article className="stat-card">
      <div className="stat-card-top">
        <div>
          <p className="stat-label">{label}</p>
          <p className="stat-value">{value}</p>
          <p className="muted-caption">{hint}</p>
        </div>
        <span className="icon-badge">{icon}</span>
      </div>
    </article>
  )
}

export default StatCard
