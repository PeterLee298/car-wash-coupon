// utils/request.js

const baseUrl = 'http://localhost:8080';

// 发送请求的通用方法
function request(options) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': wx.getStorageSync('token')
      },
      success(res) {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          // 未授权，跳转到登录页
          wx.removeStorageSync('token');
          wx.removeStorageSync('merchantInfo');
          wx.navigateTo({
            url: '/pages/login/login'
          });
          reject(new Error('未授权'));
        } else {
          reject(new Error(`请求失败: ${res.statusCode}`));
        }
      },
      fail(err) {
        reject(err);
      }
    });
  });
}

// GET请求
request.get = function(url, data) {
  return request({
    url,
    method: 'GET',
    data
  });
};

// POST请求
request.post = function(url, data) {
  return request({
    url,
    method: 'POST',
    data
  });
};

// PUT请求
request.put = function(url, data) {
  return request({
    url,
    method: 'PUT',
    data
  });
};

// DELETE请求
request.delete = function(url, data) {
  return request({
    url,
    method: 'DELETE',
    data
  });
};

module.exports = request;
module.exports.baseUrl = baseUrl;