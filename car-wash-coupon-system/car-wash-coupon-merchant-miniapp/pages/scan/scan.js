const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    scanning: false
  },

  onShow() {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
  },

  onScanCode() {
    if (this.data.scanning) return
    
    this.setData({ scanning: true })
    
    wx.scanCode({
      onlyFromCamera: true,
      scanType: ['qrCode'],
      success: (res) => {
        const code = res.result
        this.verifyCouponCode(code)
      },
      fail: (err) => {
        console.log('扫码失败', err)
      },
      complete: () => {
        this.setData({ scanning: false })
      }
    })
  },

  onInputCode() {
    wx.showModal({
      title: '输入券码',
      editable: true,
      placeholderText: '请输入卡券编码',
      success: (res) => {
        if (res.confirm && res.content) {
          this.verifyCouponCode(res.content.trim())
        }
      }
    })
  },

  verifyCouponCode(code) {
    wx.showLoading({ title: '验证中...' })
    
    request({
      url: `/verification/verify/${code}`,
      method: 'GET',
      needAuth: true
    }).then(res => {
      wx.hideLoading()
      
      const coupon = res.data
      wx.showModal({
        title: '验证成功',
        content: `卡券：${coupon.name}\n券码：${coupon.code}`,
        confirmText: '去核销',
        success: (modalRes) => {
          if (modalRes.confirm) {
            this.submitVerification(coupon.id)
          }
        }
      })
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({ title: err.message || '验证失败', icon: 'none' })
    })
  },

  submitVerification(couponId) {
    wx.showLoading({ title: '提交中...' })
    
    request({
      url: '/verification/submit',
      method: 'POST',
      data: { couponId },
      needAuth: true
    }).then(res => {
      wx.hideLoading()
      
      const verificationId = res.data
      wx.navigateTo({
        url: `/pages/verify/verify?id=${verificationId}`
      })
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({ title: err.message || '提交失败', icon: 'none' })
    })
  }
})
