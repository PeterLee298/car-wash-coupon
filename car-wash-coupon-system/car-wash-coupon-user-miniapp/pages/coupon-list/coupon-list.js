const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    coupons: [],
    loading: false,
    status: 1,
    page: 1,
    size: 10,
    hasMore: true
  },

  onLoad(options) {
    const status = options.status ? parseInt(options.status) : 1
    this.setData({ status })
    this.loadCoupons()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadCoupons().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore()
    }
  },

  loadCoupons() {
    const { status, page, size } = this.data
    this.setData({ loading: true })

    return request({
      url: '/coupons',
      method: 'GET',
      data: { status, page, size },
      needAuth: true
    }).then(res => {
      const coupons = res.data.records || []
      this.setData({
        coupons: page === 1 ? coupons : [...this.data.coupons, ...coupons],
        hasMore: coupons.length >= size
      })
    }).catch(err => {
      wx.showToast({ title: err.message || '加载失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  loadMore() {
    this.setData({ page: this.data.page + 1 })
    this.loadCoupons()
  },

  onTabChange(e) {
    const status = parseInt(e.currentTarget.dataset.status)
    if (status !== this.data.status) {
      this.setData({ status, page: 1, coupons: [], hasMore: true })
      this.loadCoupons()
    }
  },

  onCouponTap(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/coupon-detail/coupon-detail?id=${id}`
    })
  },

  onGoExpired() {
    wx.navigateTo({
      url: '/pages/coupon-list/coupon-list?status=3'
    })
  },

  onGoUsed() {
    wx.navigateTo({
      url: '/pages/coupon-list/coupon-list?status=2'
    })
  }
})
