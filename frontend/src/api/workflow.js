import http from '@/api/http'

export function getStudentCompetitionsApi(params) {
  return http.get('/student/workflow/competitions', { params })
}

export function getMyRegistrationsApi() {
  return http.get('/student/workflow/registrations')
}

export function getMyTeamsApi() {
  return http.get('/student/workflow/teams')
}

export function getMyTeamDetailApi(teamId) {
  return http.get(`/student/workflow/teams/${teamId}`)
}

export function getTeamFormOptionsApi() {
  return http.get('/student/workflow/team-options')
}

export function submitIndividualRegistrationApi(payload) {
  return http.post('/student/workflow/registrations/individual', payload)
}

export function createTeamApi(payload) {
  return http.post('/student/workflow/teams', payload)
}

export function updateTeamApi(teamId, payload) {
  return http.put(`/student/workflow/teams/${teamId}`, payload)
}

export function addTeamMemberApi(teamId, payload) {
  return http.post(`/student/workflow/teams/${teamId}/members`, payload)
}

export function removeTeamMemberApi(teamId, userId) {
  return http.delete(`/student/workflow/teams/${teamId}/members/${userId}`)
}

export function submitTeamApi(teamId, payload) {
  return http.post(`/student/workflow/teams/${teamId}/submit`, payload)
}

export function getRegistrationReviewPageApi(params) {
  return http.get('/reviews/registrations', { params })
}

export function approveRegistrationApi(registrationId, payload) {
  return http.post(`/reviews/registrations/${registrationId}/approve`, payload)
}

export function rejectRegistrationApi(registrationId, payload) {
  return http.post(`/reviews/registrations/${registrationId}/reject`, payload)
}
