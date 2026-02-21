package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.Statement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatementRepository extends BaseMapper<Statement> {
}
