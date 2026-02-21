package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeRepository extends BaseMapper<SmsCode> {
}
