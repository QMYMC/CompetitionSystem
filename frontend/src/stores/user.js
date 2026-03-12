import { defineStore } from 'pinia'
import { getUserInfoApi, loginApi, logoutApi } from '@/api/auth'
import { getToken, removeToken, setToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    profile: null,
    roles: [],
    initialized: false,
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.token && state.profile),
    hasToken: (state) => Boolean(state.token),
    displayName: (state) => state.profile?.realName || state.profile?.username || 'Guest',
  },

  actions: {
    applyProfile(profile) {
      this.profile = profile
      this.roles = profile?.roles || []
    },

    saveToken(token) {
      this.token = token
      if (token) {
        setToken(token)
      } else {
        removeToken()
      }
    },

    clearAuth() {
      this.token = ''
      this.profile = null
      this.roles = []
      this.initialized = true
      removeToken()
    },

    async login(loginForm) {
      const response = await loginApi(loginForm)
      this.saveToken(response.token)
      this.applyProfile(response.user)
      this.initialized = true
      return response
    },

    async fetchUserProfile() {
      const profile = await getUserInfoApi()
      this.applyProfile(profile)
      this.initialized = true
      return profile
    },

    async bootstrap() {
      if (this.initialized) {
        return
      }

      if (!this.token) {
        this.initialized = true
        return
      }

      try {
        await this.fetchUserProfile()
      } catch (error) {
        this.clearAuth()
        throw error
      }
    },

    async logout() {
      try {
        if (this.token) {
          await logoutApi()
        }
      } finally {
        this.clearAuth()
      }
    },
  },
})
