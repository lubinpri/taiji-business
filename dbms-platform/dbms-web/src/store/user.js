import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))
  
  const isLoggedIn = computed(() => !!token.value)
  
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  function setPermissions(perms) {
    permissions.value = perms
    localStorage.setItem('permissions', JSON.stringify(perms))
  }
  
  function logout() {
    token.value = ''
    userInfo.value = {}
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
  }
  
  function hasPermission(permission) {
    return permissions.value.includes(permission) || permissions.value.includes('*')
  }
  
  return {
    token,
    userInfo,
    permissions,
    isLoggedIn,
    setToken,
    setUserInfo,
    setPermissions,
    logout,
    hasPermission
  }
})
