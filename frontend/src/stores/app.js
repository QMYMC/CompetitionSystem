import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    systemName: import.meta.env.VITE_APP_TITLE || '高校学科竞赛信息管理系统',
  }),
})
