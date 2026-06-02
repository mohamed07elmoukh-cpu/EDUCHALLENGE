export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

export function buildAuthHeaders(user, { includeJson = false } = {}) {
  const headers = {}

  if (includeJson) {
    headers['Content-Type'] = 'application/json'
  }

  if (user?.id) {
    headers['X-User-Id'] = String(user.id)
  } else if (user?.email) {
    headers['X-User-Email'] = user.email
  }

  return headers
}

export async function parseResponse(response) {
  const contentType = response.headers.get('content-type') || ''

  if (contentType.includes('application/json')) {
    return response.json()
  }

  return null
}
