package com.carwash.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.dto.CouponDetailDTO;
import com.carwash.coupon.entity.Coupon;
import com.carwash.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "卡券接口", description = "用户卡券相关接口")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "获取用户卡券列表")
    @GetMapping
    public ApiResponse<Page<Coupon>> getUserCoupons(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Coupon> coupons = couponService.getUserCoupons(userId, status, page, size);
        return ApiResponse.success(coupons);
    }

    @Operation(summary = "获取卡券详情")
    @GetMapping("/{id}")
    public ApiResponse<CouponDetailDTO> getCouponDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CouponDetailDTO detail = couponService.getCouponDetail(id, userId);
        return ApiResponse.success(detail);
    }

    @Operation(summary = "刷新卡券二维码")
    @PostMapping("/{id}/qrcode/refresh")
    public ApiResponse<String> refreshQrCode(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String qrCode = couponService.refreshQrCode(id, userId);
        return ApiResponse.success(qrCode);
    }
}
