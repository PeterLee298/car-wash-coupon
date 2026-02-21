package com.carwash.coupon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.entity.*;
import com.carwash.coupon.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台管理接口", description = "后台管理系统相关接口")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StoreRepository storeRepository;
    private final CouponRepository couponRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final AdminUserRepository adminUserRepository;
    private final VerificationRecordRepository verificationRecordRepository;

    public AdminController(StoreRepository storeRepository, CouponRepository couponRepository,
                          CouponTypeRepository couponTypeRepository, AdminUserRepository adminUserRepository,
                          VerificationRecordRepository verificationRecordRepository) {
        this.storeRepository = storeRepository;
        this.couponRepository = couponRepository;
        this.couponTypeRepository = couponTypeRepository;
        this.adminUserRepository = adminUserRepository;
        this.verificationRecordRepository = verificationRecordRepository;
    }

    @Operation(summary = "获取门店列表（后台）")
    @GetMapping("/stores")
    public ApiResponse<Page<Store>> getStores(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Store> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Store::getCreatedTime);
        return ApiResponse.success(storeRepository.selectPage(pageParam, wrapper));
    }

    @Operation(summary = "创建门店")
    @PostMapping("/stores")
    public ApiResponse<Store> createStore(@RequestBody Store store) {
        storeRepository.insert(store);
        return ApiResponse.success(store);
    }

    @Operation(summary = "更新门店")
    @PutMapping("/stores/{id}")
    public ApiResponse<Store> updateStore(@PathVariable Long id, @RequestBody Store store) {
        store.setId(id);
        storeRepository.updateById(store);
        return ApiResponse.success(store);
    }

    @Operation(summary = "删除门店")
    @DeleteMapping("/stores/{id}")
    public ApiResponse<Void> deleteStore(@PathVariable Long id) {
        storeRepository.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取卡券类型列表")
    @GetMapping("/coupon-types")
    public ApiResponse<List<CouponType>> getCouponTypes() {
        LambdaQueryWrapper<CouponType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponType::getStatus, 1);
        return ApiResponse.success(couponTypeRepository.selectList(wrapper));
    }

    @Operation(summary = "创建卡券类型")
    @PostMapping("/coupon-types")
    public ApiResponse<CouponType> createCouponType(@RequestBody CouponType couponType) {
        couponTypeRepository.insert(couponType);
        return ApiResponse.success(couponType);
    }

    @Operation(summary = "获取卡券列表（后台）")
    @GetMapping("/coupons")
    public ApiResponse<Page<Coupon>> getCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Coupon> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        wrapper.orderByDesc(Coupon::getCreatedTime);
        return ApiResponse.success(couponRepository.selectPage(pageParam, wrapper));
    }

    @Operation(summary = "获取核销记录列表（后台）")
    @GetMapping("/verifications")
    public ApiResponse<Page<VerificationRecord>> getVerifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<VerificationRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VerificationRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(VerificationRecord::getStatus, status);
        }
        wrapper.orderByDesc(VerificationRecord::getCreatedTime);
        return ApiResponse.success(verificationRecordRepository.selectPage(pageParam, wrapper));
    }

    @Operation(summary = "审核核销记录")
    @PostMapping("/verifications/{id}/audit")
    public ApiResponse<Void> auditVerification(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        Long auditorId = (Long) request.getAttribute("userId");
        VerificationRecord record = verificationRecordRepository.selectById(id);
        if (record == null) {
            return ApiResponse.error("核销记录不存在");
        }
        record.setStatus(status);
        record.setAuditRemark(remark);
        record.setAuditorId(auditorId);
        verificationRecordRepository.updateById(record);
        return ApiResponse.success();
    }
}
