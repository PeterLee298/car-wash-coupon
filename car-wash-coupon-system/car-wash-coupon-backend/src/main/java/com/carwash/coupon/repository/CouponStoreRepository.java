package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.CouponStore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponStoreRepository extends BaseMapper<CouponStore> {
}
