import http from '@/api/http'

export function getUserPageApi(params) {
  return http.get('/users', { params })
}

export function getUserDetailApi(id) {
  return http.get(`/users/${id}`)
}

export function createUserApi(payload) {
  return http.post('/users', payload)
}

export function updateUserApi(id, payload) {
  return http.put(`/users/${id}`, payload)
}

export function deleteUserApi(id) {
  return http.delete(`/users/${id}`)
}

export function getUserOptionsApi() {
  return http.get('/users/options')
}
