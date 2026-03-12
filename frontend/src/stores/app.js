import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    systemName: import.meta.env.VITE_APP_TITLE || 'Competition System',
  }),
})
