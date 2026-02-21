App({
  globalData: {
    userInfo: null,
    token: '',
    baseUrl: 'http://localhost:8080/api',
    storeInfo: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
      this.loadStoreInfo()
    }
  },

  loadStoreInfo() {
    const storeInfo = wx.getStorageSync('storeInfo')
    if (storeInfo) {
      this.globalData.storeInfo = storeInfo
    }
  },

  logout() {
    this.globalData.token = ''
    this.globalData.userInfo = null
    this.globalData.storeInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('storeInfo')
  },

  isLoggedIn() {
    return !!this.globalData.token
  },

  setStoreInfo(storeInfo) {
    this.globalData.storeInfo = storeInfo
    wx.setStorageSync('storeInfo', storeInfo)
  }
})
