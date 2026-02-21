// pages/verify-record/verify-record.js
const request = require('../../utils/request');

Page({
  data: {
    records: [],
    currentMonth: '全部记录',
    isLoading: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    showMonthPicker: false,
    months: []
  },

  onLoad() {
    this.generateMonths();
    this.loadRecords();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, records: [], hasMore: true });
    this.loadRecords(true);
  },

  onReachBottom() {
    if (!this.data.isLoading && this.data.hasMore) {
      this.setData({ page: this.data.page + 1 });
      this.loadRecords();
    }
  },

  generateMonths() {
    const months = ['全部记录'];
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;

    // 生成最近12个月
    for (let i = 0; i < 12; i++) {
      const year = currentYear - Math.floor((i + currentMonth - 1) / 12);
      const month = ((currentMonth - 1 - i) % 12 + 12) % 12 + 1;
      months.push(`${year}-${month.toString().padStart(2, '0')}`);
    }

    this.setData({ months });
  },

  async loadRecords(refresh = false) {
    if (this.data.isLoading) return;

    this.setData({ isLoading: true });

    try {
      const res = await request.get('/api/verification/records', {
        page: this.data.page,
        pageSize: this.data.pageSize,
        month: this.data.currentMonth === '全部记录' ? '' : this.data.currentMonth
      });

      if (res.success) {
        const newRecords = res.data.records || [];
        const records = refresh ? newRecords : this.data.records.concat(newRecords);
        this.setData({
          records,
          hasMore: newRecords.length === this.data.pageSize
        });
      } else {
        wx.showToast({
          title: res.message || '获取记录失败',
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
      if (refresh) {
        wx.stopPullDownRefresh();
      }
    }
  },

  showMonthPicker() {
    this.setData({ showMonthPicker: true });
  },

  hideMonthPicker() {
    this.setData({ showMonthPicker: false });
  },

  selectMonth(e) {
    const month = e.currentTarget.dataset.month;
    this.setData({ 
      currentMonth: month, 
      showMonthPicker: false,
      page: 1, 
      records: [], 
      hasMore: true 
    });
    this.loadRecords();
  },

  navigateToDetail(e) {
    const recordId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/verify-detail/verify-detail?id=${recordId}`
    });
  },

  uploadPhotos(e) {
    const record = e.currentTarget.dataset.record;
    wx.navigateTo({
      url: `/pages/verify/verify?couponCode=${record.couponCode}`
    });
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
  }
});