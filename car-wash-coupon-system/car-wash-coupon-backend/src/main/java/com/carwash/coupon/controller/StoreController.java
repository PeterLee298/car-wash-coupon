package com.carwash.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.dto.StoreDTO;
import com.carwash.coupon.entity.Store;
import com.carwash.coupon.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "门店接口", description = "门店相关接口")
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @Operation(summary = "获取门店列表")
    @GetMapping
    public ApiResponse<Page<Store>> getStores(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Store> stores = storeService.getStores(page, size);
        return ApiResponse.success(stores);
    }

    @Operation(summary = "获取附近门店")
    @GetMapping("/nearby")
    public ApiResponse<List<StoreDTO>> getNearbyStores(
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(defaultValue = "3") Integer limit) {
        List<StoreDTO> stores = storeService.getNearbyStores(longitude, latitude, limit);
        return ApiResponse.success(stores);
    }

    @Operation(summary = "获取门店详情")
    @GetMapping("/{id}")
    public ApiResponse<StoreDTO> getStoreDetail(@PathVariable Long id) {
        StoreDTO store = storeService.getStoreDetail(id);
        return ApiResponse.success(store);
    }
}
