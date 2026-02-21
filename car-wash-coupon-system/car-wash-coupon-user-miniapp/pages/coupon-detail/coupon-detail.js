const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    couponId: null,
    couponDetail: null,
    qrCodeExpireTime: 0,
    countdown: 0,
    loading: false
  },

  onLoad(options) {
    this.setData({ couponId: options.id })
    this.loadCouponDetail()
  },

  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },

  loadCouponDetail() {
    this.setData({ loading: true })
    
    request({
      url: `/coupons/${this.data.couponId}`,
      method: 'GET',
      needAuth: true
    }).then(res => {
      this.setData({
        couponDetail: res.data,
        qrCodeExpireTime: res.data.qrCodeExpireTime
      })
      this.startCountdown()
    }).catch(err => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  startCountdown() {
    this.updateCountdown()
    this.timer = setInterval(() => {
      this.updateCountdown()
    }, 1000)
  },

  updateCountdown() {
    const now = Date.now()
    const expireTime = this.data.qrCodeExpireTime
    const remaining = Math.max(0, Math.floor((expireTime - now) / 1000))
    
    this.setData({ countdown: remaining })
    
    if (remaining <= 0) {
      clearInterval(this.timer)
    }
  },

  refreshQrCode() {
    request({
      url: `/coupons/${this.data.couponId}/qrcode/refresh`,
      method: 'POST',
      needAuth: true
    }).then(res => {
      this.setData({
        'couponDetail.qrCodeUrl': res.data,
        qrCodeExpireTime: Date.now() + 5 * 60 * 1000
      })
      this.startCountdown()
      wx.showToast({ title: '已刷新', icon: 'success' })
    }).catch(err => {
      wx.showToast({ title: err.message || '刷新失败', icon: 'none' })
    })
  },

  onStoreTap(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/store-detail/store-detail?id=${id}`
    })
  },

  onCallStore(e) {
    const { phone } = e.currentTarget.dataset
    wx.makePhoneCall({ phoneNumber: phone })
  },

  onNavigateStore(e) {
    const { latitude, longitude, name } = e.currentTarget.dataset
    wx.openLocation({
      latitude,
      longitude,
      name,
      scale: 18
    })
  },

  onMoreStores() {
    wx.navigateTo({
      url: `/pages/store-list/store-list?couponId=${this.data.couponId}`
    })
  }
})
