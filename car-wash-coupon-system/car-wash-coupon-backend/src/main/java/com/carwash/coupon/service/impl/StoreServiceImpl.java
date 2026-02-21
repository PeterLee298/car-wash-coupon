package com.carwash.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.StoreDTO;
import com.carwash.coupon.entity.Store;
import com.carwash.coupon.exception.BusinessException;
import com.carwash.coupon.repository.StoreRepository;
import com.carwash.coupon.service.StoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public Page<Store> getStores(Integer page, Integer size) {
        Page<Store> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1)
               .orderByDesc(Store::getCreatedTime);
        return storeRepository.selectPage(pageParam, wrapper);
    }

    @Override
    public List<StoreDTO> getNearbyStores(BigDecimal longitude, BigDecimal latitude, Integer limit) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1)
               .isNotNull(Store::getLongitude)
               .isNotNull(Store::getLatitude)
               .last("LIMIT " + limit);
        
        List<Store> stores = storeRepository.selectList(wrapper);
        
        return stores.stream().map(store -> {
            StoreDTO dto = new StoreDTO();
            dto.setId(store.getId());
            dto.setName(store.getName());
            dto.setAddress(store.getAddress());
            dto.setLongitude(store.getLongitude());
            dto.setLatitude(store.getLatitude());
            dto.setContactPhone(store.getContactPhone());
            dto.setBusinessHours(store.getBusinessHours());
            
            if (longitude != null && latitude != null && 
                store.getLongitude() != null && store.getLatitude() != null) {
                double distance = calculateDistance(
                    latitude.doubleValue(), longitude.doubleValue(),
                    store.getLatitude().doubleValue(), store.getLongitude().doubleValue()
                );
                dto.setDistance(distance);
            }
            
            return dto;
        }).sorted((a, b) -> {
            if (a.getDistance() == null) return 1;
            if (b.getDistance() == null) return -1;
            return Double.compare(a.getDistance(), b.getDistance());
        }).collect(Collectors.toList());
    }

    @Override
    public StoreDTO getStoreDetail(Long storeId) {
        Store store = storeRepository.selectById(storeId);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setAddress(store.getAddress());
        dto.setLongitude(store.getLongitude());
        dto.setLatitude(store.getLatitude());
        dto.setContactPhone(store.getContactPhone());
        dto.setBusinessHours(store.getBusinessHours());
        return dto;
    }

    @Override
    public Store createStore(Store store) {
        store.setStatus(1);
        storeRepository.insert(store);
        return store;
    }

    @Override
    public Store updateStore(Long storeId, Store store) {
        Store existingStore = storeRepository.selectById(storeId);
        if (existingStore == null) {
            throw new BusinessException("门店不存在");
        }
        
        store.setId(storeId);
        storeRepository.updateById(store);
        return store;
    }

    @Override
    public void deleteStore(Long storeId) {
        Store store = storeRepository.selectById(storeId);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        storeRepository.deleteById(storeId);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
}
