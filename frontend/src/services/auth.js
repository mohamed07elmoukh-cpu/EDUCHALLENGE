import { API_BASE_URL, parseResponse } from './api'

export async function loginUser(payload) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Login failed')
    error.status = response.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return data
}

export async function registerUser(payload) {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  const data = await parseResponse(response)

  if (!response.ok) {
    const error = new Error(data?.message || 'Registration failed')
    error.status = response.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return data
}
