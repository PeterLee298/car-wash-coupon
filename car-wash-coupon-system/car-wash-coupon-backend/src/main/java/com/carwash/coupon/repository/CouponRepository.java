package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponRepository extends BaseMapper<Coupon> {
}
