const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    storeInfo: null,
    todayCount: 0,
    monthCount: 0
  },

  onLoad() {
    this.checkLogin()
  },

  onShow() {
    this.checkLogin()
    if (app.isLoggedIn()) {
      this.loadStatistics()
    }
  },

  checkLogin() {
    const isLoggedIn = app.isLoggedIn()
    this.setData({
      isLoggedIn,
      userInfo: app.globalData.userInfo,
      storeInfo: app.globalData.storeInfo
    })
  },

  loadStatistics() {
    const today = new Date()
    const yearMonth = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}`
    
    request({
      url: '/verification/records',
      method: 'GET',
      data: { yearMonth, page: 1, size: 100 },
      needAuth: true
    }).then(res => {
      const records = res.data.records || []
      const todayStr = this.formatDate(today)
      const todayRecords = records.filter(r => r.verifyTime && r.verifyTime.startsWith(todayStr))
      
      this.setData({
        monthCount: records.length,
        todayCount: todayRecords.length
      })
    })
  },

  formatDate(date) {
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  onLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onScan() {
    if (!app.isLoggedIn()) {
      this.onLogin()
      return
    }
    wx.switchTab({
      url: '/pages/scan/scan'
    })
  },

  onViewRecords() {
    if (!app.isLoggedIn()) {
      this.onLogin()
      return
    }
    wx.navigateTo({
      url: '/pages/verify-record/verify-record'
    })
  }
})
