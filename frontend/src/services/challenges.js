import { API_BASE_URL, buildAuthHeaders, parseResponse } from './api'

function normalizeDifficulty(difficulty) {
  if (!difficulty) {
    return 'Medium'
  }

  return difficulty.charAt(0).toUpperCase() + difficulty.slice(1).toLowerCase()
}

function normalizeBackendChallenge(challenge) {
  return {
    ...challenge,
    id: String(challenge.id),
    difficulty: normalizeDifficulty(challenge.difficulty),
    duration: challenge.duration || 'Flexible',
    participants: challenge.participants || 0,
    status: challenge.status || challenge.visibility || 'Created',
    questions: Array.isArray(challenge.questions) ? challenge.questions : [],
    comments: Array.isArray(challenge.comments) ? challenge.comments : [],
    social: challenge.social || null,
  }
}

export async function fetchChallenges(user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges`, {
    headers: buildAuthHeaders(user),
  })
  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Unable to load challenges')
    error.status = response.status
    throw error
  }

  return Array.isArray(data) ? data.map(normalizeBackendChallenge) : []
}

export async function fetchChallengeById(challengeId, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}`, {
    headers: buildAuthHeaders(user),
  })
  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Unable to load challenge details')
    error.status = response.status
    throw error
  }

  return normalizeBackendChallenge(data)
}

export async function createChallenge(payload, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges`, {
    method: 'POST',
    headers: buildAuthHeaders(user, { includeJson: true }),
    body: JSON.stringify(payload),
  })

  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Unable to create challenge')
    error.status = response.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return normalizeBackendChallenge(data)
}

export async function submitChallengeAttempt(challengeId, answers, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}/attempts`, {
    method: 'POST',
    headers: buildAuthHeaders(user, { includeJson: true }),
    body: JSON.stringify({ answers }),
  })

  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Unable to submit challenge answers')
    error.status = response.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return data
}

export async function fetchMyCreatedChallenges(user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/my-created`, {
    headers: buildAuthHeaders(user),
  })
  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Unable to load your created challenges')
    error.status = response.status
    throw error
  }

  return Array.isArray(data)
    ? data.map((challenge) => ({
        ...challenge,
        id: String(challenge.id),
        difficulty: normalizeDifficulty(challenge.difficulty),
        pointsReward: Number(challenge.pointsReward || 0),
        questionsCount: Number(challenge.questionsCount || 0),
        participantsCount: Number(challenge.participantsCount || 0),
        attemptsCount: Number(challenge.attemptsCount || 0),
        status: challenge.status || 'ACTIVE',
      }))
    : []
}

export async function deleteChallenge(challengeId, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}`, {
    method: 'DELETE',
    headers: buildAuthHeaders(user),
  })

  if (!response.ok) {
    const data = await parseResponse(response)
    const error = new Error(data?.message || 'Unable to delete challenge')
    error.status = response.status
    throw error
  }
}

async function parseChallengeMutationResponse(response, fallbackMessage) {
  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || fallbackMessage)
    error.status = response.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return data
}

export async function toggleChallengeLike(challengeId, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}/likes/toggle`, {
    method: 'POST',
    headers: buildAuthHeaders(user),
  })

  return parseChallengeMutationResponse(response, 'Unable to update challenge like')
}

export async function toggleSavedChallenge(challengeId, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}/saves/toggle`, {
    method: 'POST',
    headers: buildAuthHeaders(user),
  })

  return parseChallengeMutationResponse(response, 'Unable to update saved challenge')
}

export async function shareChallenge(challengeId, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}/shares`, {
    method: 'POST',
    headers: buildAuthHeaders(user),
  })

  return parseChallengeMutationResponse(response, 'Unable to register challenge share')
}

export async function createChallengeComment(challengeId, payload, user) {
  const response = await fetch(`${API_BASE_URL}/api/challenges/${challengeId}/comments`, {
    method: 'POST',
    headers: buildAuthHeaders(user, { includeJson: true }),
    body: JSON.stringify(payload),
  })

  return parseChallengeMutationResponse(response, 'Unable to publish comment')
}
