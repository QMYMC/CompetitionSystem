import http from '@/api/http'

export function getCompetitionPageApi(params) {
  return http.get('/competitions', { params })
}

export function getCompetitionDetailApi(id) {
  return http.get(`/competitions/${id}`)
}

export function createCompetitionApi(payload) {
  return http.post('/competitions', payload)
}

export function updateCompetitionApi(id, payload) {
  return http.put(`/competitions/${id}`, payload)
}

export function deleteCompetitionApi(id) {
  return http.delete(`/competitions/${id}`)
}

export function getCompetitionOptionsApi() {
  return http.get('/competitions/options')
}
