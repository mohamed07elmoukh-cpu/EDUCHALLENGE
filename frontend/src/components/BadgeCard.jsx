function formatCondition(badge) {
  if (!badge?.conditionType) {
    return ''
  }

  switch (badge.conditionType) {
    case 'CHALLENGES_COMPLETED':
      return `Complete ${badge.conditionValue} challenge(s)`
    case 'POINTS_REACHED':
      return `Reach ${badge.conditionValue} points`
    case 'PERFECT_SCORE':
      return 'Finish with a perfect score'
    case 'FIRST_TRY_PERFECT':
      return 'Perfect score on the first try'
    case 'STREAK_REACHED':
      return `Reach a ${badge.conditionValue}-day streak`
    default:
      return badge.conditionType
  }
}

function formatEarnedAt(value) {
  if (!value) {
    return ''
  }

  try {
    return new Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    }).format(new Date(value))
  } catch {
    return ''
  }
}

function BadgeCard({ badge }) {
  const tone = badge?.tone || (badge?.earned ? 'bronze' : 'locked')
  const conditionLabel = formatCondition(badge)
  const earnedAtLabel = formatEarnedAt(badge?.earnedAt)
  const iconLabel = (badge?.iconName || badge?.title || 'BD').slice(0, 2).toUpperCase()

  return (
    <article className={`badge-card ${tone}`}>
      <div className="badge-top">
        <div className="badge-heading">
          <span className="badge-icon" aria-hidden="true">
            {iconLabel}
          </span>
          <div className="badge-heading-copy">
            <strong>{badge.title}</strong>
            <p className="muted-caption">{badge.badgeType || 'achievement'}</p>
          </div>
        </div>
        <span className="tag badge-state-tag">{badge.earned ? 'Unlocked' : 'Locked'}</span>
      </div>
      <p className="muted-caption">{badge.description}</p>
      {conditionLabel && <p className="muted-caption">{conditionLabel}</p>}
      {earnedAtLabel && <p className="muted-caption">Unlocked on {earnedAtLabel}</p>}
    </article>
  )
}

export default BadgeCard
