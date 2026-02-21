const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    couponCount: 0
  },

  onLoad() {
    this.checkLogin()
  },

  onShow() {
    this.checkLogin()
    if (app.isLoggedIn()) {
      this.loadCouponCount()
    }
  },

  checkLogin() {
    const isLoggedIn = app.isLoggedIn()
    this.setData({ 
      isLoggedIn,
      userInfo: app.globalData.userInfo
    })
  },

  loadCouponCount() {
    request({
      url: '/coupons',
      method: 'GET',
      data: { status: 1, page: 1, size: 1 },
      needAuth: true
    }).then(res => {
      this.setData({ couponCount: res.data.total || 0 })
    })
  },

  onLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onMyCoupons() {
    if (!app.isLoggedIn()) {
      this.onLogin()
      return
    }
    wx.navigateTo({
      url: '/pages/coupon-list/coupon-list'
    })
  },

  onAppointment() {
    if (!app.isLoggedIn()) {
      this.onLogin()
      return
    }
    wx.navigateTo({
      url: '/pages/store-list/store-list'
    })
  },

  onContactService() {
    wx.showModal({
      title: '联系客服',
      content: '客服电话：400-123-4567',
      confirmText: '拨打电话',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '4001234567'
          })
        }
      }
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
            couponCount: 0
          })
          wx.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  }
})
