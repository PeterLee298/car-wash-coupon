const app = getApp()

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    storeInfo: null
  },

  onLoad() {
    this.checkLogin()
  },

  onShow() {
    this.checkLogin()
  },

  checkLogin() {
    const isLoggedIn = app.isLoggedIn()
    this.setData({
      isLoggedIn,
      userInfo: app.globalData.userInfo,
      storeInfo: app.globalData.storeInfo
    })
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
          this.setData({
            isLoggedIn: false,
            userInfo: null,
            storeInfo: null
          })
          wx.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  }
})
