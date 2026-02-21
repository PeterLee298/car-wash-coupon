package com.carwash.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.StoreDTO;
import com.carwash.coupon.entity.Store;

import java.math.BigDecimal;
import java.util.List;

public interface StoreService {
    Page<Store> getStores(Integer page, Integer size);
    List<StoreDTO> getNearbyStores(BigDecimal longitude, BigDecimal latitude, Integer limit);
    StoreDTO getStoreDetail(Long storeId);
    Store createStore(Store store);
    Store updateStore(Long storeId, Store store);
    void deleteStore(Long storeId);
}
