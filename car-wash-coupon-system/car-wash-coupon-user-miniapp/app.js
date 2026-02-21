App({
  globalData: {
    userInfo: null,
    token: '',
    baseUrl: 'http://localhost:8080/api'
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
      this.checkLoginStatus()
    }
  },

  checkLoginStatus() {
    wx.request({
      url: `${this.globalData.baseUrl}/auth/check`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${this.globalData.token}`
      },
      success: (res) => {
        if (res.data.code !== 200) {
          this.logout()
        }
      },
      fail: () => {
        this.logout()
      }
    })
  },

  logout() {
    this.globalData.token = ''
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  isLoggedIn() {
    return !!this.globalData.token
  },

  requireLogin(callback) {
    if (this.isLoggedIn()) {
      callback && callback()
    } else {
      wx.navigateTo({
        url: '/pages/login/login'
      })
    }
  }
})
