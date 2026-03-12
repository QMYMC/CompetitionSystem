import http from '@/api/http'

export function getStudentAwardOptionsApi() {
  return http.get('/student/awards/options')
}

export function getMyAwardsApi() {
  return http.get('/student/awards')
}

export function createAwardApi(payload) {
  return http.post('/student/awards', payload)
}

export function updateAwardApi(awardId, payload) {
  return http.put(`/student/awards/${awardId}`, payload)
}

export function getAwardReviewPageApi(params) {
  return http.get('/awards/reviews', { params })
}

export function approveAwardApi(awardId, payload) {
  return http.post(`/awards/reviews/${awardId}/approve`, payload)
}

export function rejectAwardApi(awardId, payload) {
  return http.post(`/awards/reviews/${awardId}/reject`, payload)
}
