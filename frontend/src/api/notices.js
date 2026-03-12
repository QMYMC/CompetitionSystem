import http from '@/api/http'

export function getNoticePageApi(params) {
  return http.get('/notices', { params })
}

export function getNoticeDetailApi(noticeId) {
  return http.get(`/notices/${noticeId}`)
}

export function createNoticeApi(payload) {
  return http.post('/notices', payload)
}

export function updateNoticeApi(noticeId, payload) {
  return http.put(`/notices/${noticeId}`, payload)
}

export function deleteNoticeApi(noticeId) {
  return http.delete(`/notices/${noticeId}`)
}
