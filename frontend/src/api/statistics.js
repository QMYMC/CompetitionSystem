import http from '@/api/http'

export function getStatisticsOverviewApi() {
  return http.get('/statistics/overview')
}
