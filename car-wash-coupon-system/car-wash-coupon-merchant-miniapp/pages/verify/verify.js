// pages/verify/verify.js
const request = require('../../utils/request');

Page({
  data: {
    couponCode: '',
    couponInfo: null,
    storeInfo: null,
    photos: [],
    maxPhotos: 9,
    isSubmitting: false
  },

  onLoad(options) {
    if (options.couponCode) {
      this.setData({ couponCode: options.couponCode });
      this.verifyCoupon(options.couponCode);
    }
  },

  async verifyCoupon(couponCode) {
    try {
      const res = await request.post('/api/verification/verify-coupon', {
        couponCode
      });
      if (res.success) {
        this.setData({
          couponInfo: res.data.coupon,
          storeInfo: res.data.store
        });
      } else {
        wx.showToast({
          title: res.message || '券码验证失败',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    } catch (error) {
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    }
  },

  chooseImage() {
    const that = this;
    wx.chooseImage({
      count: this.data.maxPhotos - this.data.photos.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        const tempFilePaths = res.tempFilePaths;
        const newPhotos = that.data.photos.concat(tempFilePaths);
        that.setData({ photos: newPhotos });
      }
    });
  },

  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const photos = this.data.photos;
    photos.splice(index, 1);
    this.setData({ photos });
  },

  async submitVerify() {
    if (this.data.photos.length === 0) {
      wx.showToast({
        title: '请至少上传一张施工图片',
        icon: 'none'
      });
      return;
    }

    this.setData({ isSubmitting: true });

    try {
      // 先上传图片
      const uploadPromises = this.data.photos.map((photo, index) => {
        return new Promise((resolve, reject) => {
          wx.uploadFile({
            url: request.baseUrl + '/api/verification/upload-photo',
            filePath: photo,
            name: 'photo',
            header: {
              'Authorization': wx.getStorageSync('token')
            },
            formData: {
              couponCode: this.data.couponCode
            },
            success(res) {
              const data = JSON.parse(res.data);
              if (data.success) {
                resolve(data.data);
              } else {
                reject(new Error(data.message));
              }
            },
            fail(err) {
              reject(err);
            }
          });
        });
      });

      const uploadedPhotos = await Promise.all(uploadPromises);

      // 提交核销
      const res = await request.post('/api/verification/submit-verify', {
        couponCode: this.data.couponCode,
        photoUrls: uploadedPhotos
      });

      if (res.success) {
        wx.showToast({
          title: '核销提交成功',
          icon: 'success'
        });
        setTimeout(() => {
          wx.navigateTo({
            url: '/pages/verify-detail/verify-detail?id=' + res.data.verificationId
          });
        }, 1500);
      } else {
        wx.showToast({
          title: res.message || '核销提交失败',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    } finally {
      this.setData({ isSubmitting: false });
    }
  }
});