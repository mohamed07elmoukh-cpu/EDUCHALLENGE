import { API_BASE_URL, buildAuthHeaders, parseResponse } from './api'

function buildAdminError(data, fallbackMessage, response) {
  const error = new Error(data?.message || fallbackMessage)
  error.status = response.status
  error.fieldErrors = data?.fieldErrors || {}
  throw error
}

async function fetchAdminResource(path, user, options = {}, fallbackMessage = 'Admin request failed') {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...buildAuthHeaders(user, { includeJson: options?.body != null }),
      ...(options.headers || {}),
    },
  })
  const data = await parseResponse(response)

  if (!response.ok) {
    buildAdminError(data, fallbackMessage, response)
  }

  return data
}

export async function fetchAdminChallenges(user) {
  const data = await fetchAdminResource('/api/admin/challenges', user, {}, 'Unable to load admin challenges')
  return Array.isArray(data) ? data : []
}

export async function fetchAdminChallengeDetails(challengeId, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}`,
    user,
    {},
    'Unable to load admin challenge details',
  )
}

export async function fetchAdminChallengeStats(challengeId, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}/stats`,
    user,
    {},
    'Unable to load challenge stats',
  )
}

export async function fetchAdminChallengeAttempts(challengeId, user) {
  const data = await fetchAdminResource(
    `/api/admin/challenges/${challengeId}/attempts`,
    user,
    {},
    'Unable to load challenge attempts',
  )
  return Array.isArray(data) ? data : []
}

export async function fetchAdminChallengeLeaderboard(challengeId, user) {
  const data = await fetchAdminResource(
    `/api/admin/challenges/${challengeId}/leaderboard`,
    user,
    {},
    'Unable to load challenge leaderboard',
  )
  return Array.isArray(data) ? data : []
}

export async function createAdminChallenge(payload, user) {
  return fetchAdminResource(
    '/api/admin/challenges',
    user,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    'Unable to create admin challenge',
  )
}

export async function addAdminChallengeStep(challengeId, payload, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}/steps`,
    user,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    'Unable to add challenge step',
  )
}

export async function updateAdminChallenge(challengeId, payload, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}`,
    user,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    'Unable to update challenge',
  )
}

export async function updateAdminChallengeStep(challengeId, stepId, payload, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}/steps/${stepId}`,
    user,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    'Unable to update challenge step',
  )
}

export async function addAdminChallengeOption(challengeId, stepId, payload, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}/steps/${stepId}/options`,
    user,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    'Unable to add challenge option',
  )
}

export async function updateAdminChallengeOption(challengeId, stepId, optionId, payload, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}/steps/${stepId}/options/${optionId}`,
    user,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    'Unable to update challenge option',
  )
}

export async function deleteAdminChallenge(challengeId, user) {
  return fetchAdminResource(
    `/api/admin/challenges/${challengeId}`,
    user,
    {
      method: 'DELETE',
    },
    'Unable to delete challenge',
  )
}
