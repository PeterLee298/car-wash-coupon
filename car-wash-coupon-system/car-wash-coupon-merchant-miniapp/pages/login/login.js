const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    phone: '',
    code: '',
    countdown: 0,
    canSendCode: true,
    loading: false
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value })
  },

  validatePhone(phone) {
    return /^1[3-9]\d{9}$/.test(phone)
  },

  sendCode() {
    const { phone, canSendCode, countdown } = this.data
    
    if (!canSendCode || countdown > 0) return
    
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    
    if (!this.validatePhone(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }

    request({
      url: '/auth/sms/send',
      method: 'POST',
      data: {
        phone,
        type: 'merchant_login'
      }
    }).then(() => {
      wx.showToast({ title: '验证码已发送', icon: 'success' })
      this.startCountdown()
    }).catch(err => {
      wx.showToast({ title: err.message || '发送失败', icon: 'none' })
    })
  },

  startCountdown() {
    this.setData({ countdown: 60, canSendCode: false })
    
    const timer = setInterval(() => {
      const countdown = this.data.countdown - 1
      if (countdown <= 0) {
        clearInterval(timer)
        this.setData({ countdown: 0, canSendCode: true })
      } else {
        this.setData({ countdown })
      }
    }, 1000)
  },

  login() {
    const { phone, code, loading } = this.data
    
    if (loading) return
    
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    
    if (!this.validatePhone(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }
    
    if (!code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }

    this.setData({ loading: true })

    request({
      url: '/auth/merchant/login',
      method: 'POST',
      data: { phone, code }
    }).then(res => {
      const { token, ...userInfo } = res.data
      app.globalData.token = token
      app.globalData.userInfo = userInfo
      wx.setStorageSync('token', token)
      wx.setStorageSync('userInfo', userInfo)
      
      wx.showToast({ title: '登录成功', icon: 'success' })
      
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    }).catch(err => {
      wx.showToast({ title: err.message || '登录失败', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  }
})
