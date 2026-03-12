import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code === 200) {
      return payload.data
    }

    const message = payload?.message || 'Request failed.'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || 'Network error.'

    if (status === 401) {
      removeToken()
      if (window.location.pathname !== '/login') {
        const redirect = encodeURIComponent(window.location.pathname + window.location.search)
        window.location.assign(`/login?redirect=${redirect}`)
      }
    } else {
      ElMessage.error(message)
    }

    return Promise.reject(new Error(message))
  },
)

export default http
