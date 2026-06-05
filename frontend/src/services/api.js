export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

export function buildAuthHeaders(user, { includeJson = false } = {}) {
  const headers = {}

  if (includeJson) {
    headers['Content-Type'] = 'application/json'
  }

  if (user?.accessToken) {
    headers.Authorization = `${user.tokenType || 'Bearer'} ${user.accessToken}`
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
