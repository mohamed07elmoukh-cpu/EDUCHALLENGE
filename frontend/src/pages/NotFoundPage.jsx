import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <section className="not-found">
      <article className="surface-card" style={{ maxWidth: '560px', textAlign: 'center' }}>
        <span className="eyebrow">404</span>
        <h1>Page not found</h1>
        <p className="muted-caption">
          The page you requested does not exist in the current frontend routing.
        </p>
        <div className="details-actions" style={{ justifyContent: 'center' }}>
          <Link className="button-primary" to="/">
            Return home
          </Link>
        </div>
      </article>
    </section>
  )
}

export default NotFoundPage
