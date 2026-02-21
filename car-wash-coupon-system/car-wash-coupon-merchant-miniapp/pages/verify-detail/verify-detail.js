// pages/verify-detail/verify-detail.js
const request = require('../../utils/request');

Page({
  data: {
    recordId: '',
    recordDetail: null,
    isLoading: true
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ recordId: options.id });
      this.loadRecordDetail(options.id);
    }
  },

  async loadRecordDetail(recordId) {
    this.setData({ isLoading: true });

    try {
      const res = await request.get(`/api/verification/record/${recordId}`);
      if (res.success) {
        this.setData({ recordDetail: res.data });
      } else {
        wx.showToast({
          title: res.message || '获取详情失败',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  getStatusText(status) {
    const statusMap = {
      'PENDING': '待审核',
      'APPROVED': '已通过',
      'REJECTED': '已拒绝',
      'UPLOAD_NEEDED': '待上传照片'
    };
    return statusMap[status] || status;
  },

  getStatusClass(status) {
    const classMap = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'UPLOAD_NEEDED': 'status-upload'
    };
    return classMap[status] || '';
  },

  previewPhoto(e) {
    const photo = e.currentTarget.dataset.photo;
    const photos = this.data.recordDetail.photos || [];
    wx.previewImage({
      current: photo,
      urls: photos
    });
  },

  uploadPhotos() {
    wx.navigateTo({
      url: `/pages/verify/verify?couponCode=${this.data.recordDetail.couponCode}`
    });
  }
});