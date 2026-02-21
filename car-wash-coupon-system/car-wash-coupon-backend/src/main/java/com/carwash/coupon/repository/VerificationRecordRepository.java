package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.VerificationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerificationRecordRepository extends BaseMapper<VerificationRecord> {
}
