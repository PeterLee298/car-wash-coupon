package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.Store;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreRepository extends BaseMapper<Store> {
}
