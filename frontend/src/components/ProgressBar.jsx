function ProgressBar({ label, current, max, helper }) {
  const progress = Math.min(100, Math.round((current / max) * 100))

  return (
    <div className="progress-block">
      <div className="progress-meta">
        <div>
          <strong>{label}</strong>
          <p className="muted-caption">{helper}</p>
        </div>
        <strong>{progress}%</strong>
      </div>
      <div className="progress-track" aria-hidden="true">
        <div className="progress-fill" style={{ width: `${progress}%` }} />
      </div>
    </div>
  )
}

export default ProgressBar
