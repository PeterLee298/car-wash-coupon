const app = getApp()
const { request } = require('../../utils/request')

Page({
  data: {
    verificationRecordId: null,
    rating: 0,
    content: '',
    photos: [],
    submitting: false
  },

  onLoad(options) {
    this.setData({ verificationRecordId: options.verificationRecordId })
  },

  onRatingChange(e) {
    this.setData({ rating: e.currentTarget.dataset.rating })
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  onChoosePhoto() {
    const maxPhotos = 3
    const remaining = maxPhotos - this.data.photos.length
    
    if (remaining <= 0) {
      wx.showToast({ title: '最多上传3张照片', icon: 'none' })
      return
    }

    wx.chooseMedia({
      count: remaining,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPhotos = res.tempFiles.map(file => file.tempFilePath)
        this.setData({
          photos: [...this.data.photos, ...newPhotos]
        })
      }
    })
  },

  onRemovePhoto(e) {
    const { index } = e.currentTarget.dataset
    const photos = this.data.photos.filter((_, i) => i !== index)
    this.setData({ photos })
  },

  onSubmit() {
    const { rating, content, photos, verificationRecordId, submitting } = this.data
    
    if (submitting) return
    
    if (rating === 0) {
      wx.showToast({ title: '请选择评分', icon: 'none' })
      return
    }

    this.setData({ submitting: true })

    this.uploadPhotos(photos).then(uploadedUrls => {
      return request({
        url: '/verification/evaluation',
        method: 'POST',
        data: {
          verificationRecordId,
          rating,
          content,
          photos: uploadedUrls
        },
        needAuth: true
      })
    }).then(() => {
      wx.showToast({ title: '评价成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }).catch(err => {
      wx.showToast({ title: err.message || '评价失败', icon: 'none' })
    }).finally(() => {
      this.setData({ submitting: false })
    })
  },

  uploadPhotos(photos) {
    if (photos.length === 0) {
      return Promise.resolve([])
    }

    const uploadPromises = photos.map(photo => {
      return new Promise((resolve, reject) => {
        wx.uploadFile({
          url: `${app.globalData.baseUrl}/upload`,
          filePath: photo,
          name: 'file',
          header: {
            'Authorization': `Bearer ${app.globalData.token}`
          },
          success: (res) => {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data.url)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          },
          fail: reject
        })
      })
    })

    return Promise.all(uploadPromises)
  }
})
