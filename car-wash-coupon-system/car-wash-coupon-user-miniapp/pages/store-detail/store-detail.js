const { request } = require('../../utils/request')

Page({
  data: {
    storeId: null,
    store: null,
    loading: false
  },

  onLoad(options) {
    this.setData({ storeId: options.id })
    this.loadStoreDetail()
  },

  loadStoreDetail() {
    this.setData({ loading: true })
    
    request({
      url: `/stores/${this.data.storeId}`,
      method: 'GET'
    }).then(res => {
      this.setData({ store: res.data })
    }).catch(err => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  onCall() {
    if (this.data.store && this.data.store.contactPhone) {
      wx.makePhoneCall({
        phoneNumber: this.data.store.contactPhone
      })
    }
  },

  onNavigate() {
    if (this.data.store) {
      wx.openLocation({
        latitude: this.data.store.latitude,
        longitude: this.data.store.longitude,
        name: this.data.store.name,
        address: this.data.store.address,
        scale: 18
      })
    }
  }
})
