const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    isLoggedIn: false,
    showAgreement: false,
    activities: [],
    functionEntries: [],
    nearbyStores: []
  },

  onLoad() {
    this.checkLoginAndAgreement()
    this.loadPublicData()
  },

  onShow() {
    this.setData({
      isLoggedIn: app.isLoggedIn()
    })
    if (app.isLoggedIn()) {
      this.loadNearbyStores()
    }
  },

  checkLoginAndAgreement() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && !userInfo.agreementAccepted) {
      this.setData({ showAgreement: true })
    }
  },

  loadPublicData() {
    request({
      url: '/public/activities',
      method: 'GET'
    }).then(res => {
      this.setData({ activities: res.data || [] })
    })

    request({
      url: '/public/function-entries',
      method: 'GET'
    }).then(res => {
      this.setData({ functionEntries: res.data || [] })
    })
  },

  loadNearbyStores() {
    wx.getLocation({
      type: 'gcj02',
      success: (location) => {
        request({
          url: '/stores/nearby',
          method: 'GET',
          data: {
            longitude: location.longitude,
            latitude: location.latitude,
            limit: 3
          }
        }).then(res => {
          this.setData({ nearbyStores: res.data || [] })
        })
      },
      fail: () => {
        request({
          url: '/stores/nearby',
          method: 'GET',
          data: { limit: 3 }
        }).then(res => {
          this.setData({ nearbyStores: res.data || [] })
        })
      }
    })
  },

  onActivityTap(e) {
    const { linkUrl } = e.currentTarget.dataset
    if (linkUrl) {
      wx.navigateTo({
        url: `/pages/webview/webview?url=${encodeURIComponent(linkUrl)}`
      })
    }
  },

  onFunctionEntryTap(e) {
    const { linkUrl } = e.currentTarget.dataset
    if (linkUrl) {
      wx.navigateTo({
        url: linkUrl
      })
    }
  },

  onStoreTap(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/store-detail/store-detail?id=${id}`
    })
  },

  onCallStore(e) {
    const { phone } = e.currentTarget.dataset
    wx.makePhoneCall({
      phoneNumber: phone
    })
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
      url: '/pages/store-list/store-list'
    })
  },

  onGoLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onAcceptAgreement() {
    request({
      url: '/auth/agreement/accept',
      method: 'POST',
      needAuth: true
    }).then(() => {
      const userInfo = wx.getStorageSync('userInfo')
      userInfo.agreementAccepted = 1
      wx.setStorageSync('userInfo', userInfo)
      this.setData({ showAgreement: false })
    })
  },

  onCancelAgreement() {
    this.setData({ showAgreement: false })
  },

  onPullDownRefresh() {
    this.loadPublicData()
    if (app.isLoggedIn()) {
      this.loadNearbyStores()
    }
    wx.stopPullDownRefresh()
  }
})
