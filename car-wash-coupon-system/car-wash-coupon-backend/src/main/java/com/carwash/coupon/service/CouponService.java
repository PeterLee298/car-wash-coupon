package com.carwash.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.CouponDetailDTO;
import com.carwash.coupon.entity.Coupon;

import java.util.List;

public interface CouponService {
    Page<Coupon> getUserCoupons(Long userId, Integer status, Integer page, Integer size);
    CouponDetailDTO getCouponDetail(Long couponId, Long userId);
    String refreshQrCode(Long couponId, Long userId);
    List<Coupon> importCoupons(List<Coupon> coupons, List<Long> storeIds);
    void distributeCoupon(Long couponId, Long userId);
}
