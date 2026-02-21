const app = getApp()

function request(options) {
  const { url, method = 'GET', data = {}, needAuth = false } = options
  
  return new Promise((resolve, reject) => {
    const header = {
      'Content-Type': 'application/json'
    }
    
    if (needAuth) {
      const token = app.globalData.token || wx.getStorageSync('token')
      if (!token) {
        wx.navigateTo({ url: '/pages/login/login' })
        reject(new Error('请先登录'))
        return
      }
      header['Authorization'] = `Bearer ${token}`
    }
    
    wx.request({
      url: `${app.globalData.baseUrl}${url}`,
      method,
      data,
      header,
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else if (res.data.code === 401) {
          app.logout()
          wx.navigateTo({ url: '/pages/login/login' })
          reject(new Error('登录已过期，请重新登录'))
        } else {
          reject(new Error(res.data.message || '请求失败'))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '网络错误'))
      }
    })
  })
}

module.exports = {
  request
}
