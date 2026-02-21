package com.carwash.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carwash.coupon.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserRepository extends BaseMapper<AdminUser> {
}
