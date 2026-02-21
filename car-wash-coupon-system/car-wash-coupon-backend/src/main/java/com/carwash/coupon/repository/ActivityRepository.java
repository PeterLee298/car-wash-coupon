package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityRepository extends BaseMapper<Activity> {
}
