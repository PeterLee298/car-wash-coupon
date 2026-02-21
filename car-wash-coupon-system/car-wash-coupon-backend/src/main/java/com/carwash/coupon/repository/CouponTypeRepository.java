package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.CouponType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponTypeRepository extends BaseMapper<CouponType> {
}
