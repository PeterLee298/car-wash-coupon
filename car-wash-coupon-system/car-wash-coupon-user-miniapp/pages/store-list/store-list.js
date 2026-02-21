const { request } = require('../../utils/request')

Page({
  data: {
    stores: [],
    loading: false,
    page: 1,
    size: 10,
    hasMore: true,
    couponId: null
  },

  onLoad(options) {
    this.setData({ couponId: options.couponId })
    this.loadStores()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadStores().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore()
    }
  },

  loadStores() {
    const { page, size, couponId } = this.data
    this.setData({ loading: true })

    let url = '/stores'
    if (couponId) {
      url = `/coupons/${couponId}/stores`
    }

    return request({
      url,
      method: 'GET',
      data: { page, size },
      needAuth: !!couponId
    }).then(res => {
      const stores = res.data.records || res.data || []
      this.setData({
        stores: page === 1 ? stores : [...this.data.stores, ...stores],
        hasMore: Array.isArray(res.data.records) ? stores.length >= size : false
      })
    }).catch(err => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  loadMore() {
    this.setData({ page: this.data.page + 1 })
    this.loadStores()
  },

  onStoreTap(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/store-detail/store-detail?id=${id}`
    })
  }
})
