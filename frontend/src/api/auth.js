import http from '@/api/http'

export function loginApi(payload) {
  return http.post('/auth/login', payload)
}

export function getUserInfoApi() {
  return http.get('/auth/info')
}

export function logoutApi() {
  return http.post('/auth/logout')
}
