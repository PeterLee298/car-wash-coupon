package com.carwash.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.CouponDetailDTO;
import com.carwash.coupon.dto.StoreDTO;
import com.carwash.coupon.entity.Coupon;
import com.carwash.coupon.entity.CouponStore;
import com.carwash.coupon.entity.CouponType;
import com.carwash.coupon.entity.Store;
import com.carwash.coupon.exception.BusinessException;
import com.carwash.coupon.repository.CouponRepository;
import com.carwash.coupon.repository.CouponStoreRepository;
import com.carwash.coupon.repository.CouponTypeRepository;
import com.carwash.coupon.repository.StoreRepository;
import com.carwash.coupon.service.CouponService;
import com.carwash.coupon.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final CouponStoreRepository couponStoreRepository;
    private final StoreRepository storeRepository;
    private final QrCodeUtil qrCodeUtil;
    private final StringRedisTemplate redisTemplate;

    @Value("${qrcode.expire-minutes}")
    private Integer qrCodeExpireMinutes;

    public CouponServiceImpl(CouponRepository couponRepository, CouponTypeRepository couponTypeRepository,
                            CouponStoreRepository couponStoreRepository, StoreRepository storeRepository,
                            QrCodeUtil qrCodeUtil, StringRedisTemplate redisTemplate) {
        this.couponRepository = couponRepository;
        this.couponTypeRepository = couponTypeRepository;
        this.couponStoreRepository = couponStoreRepository;
        this.storeRepository = storeRepository;
        this.qrCodeUtil = qrCodeUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<Coupon> getUserCoupons(Long userId, Integer status, Integer page, Integer size) {
        Page<Coupon> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        } else {
            wrapper.in(Coupon::getStatus, 1, 2, 3);
        }
        
        wrapper.orderByAsc(Coupon::getEndTime);
        
        return couponRepository.selectPage(pageParam, wrapper);
    }

    @Override
    public CouponDetailDTO getCouponDetail(Long couponId, Long userId) {
        Coupon coupon = couponRepository.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("卡券不存在");
        }
        
        if (!coupon.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此卡券");
        }

        CouponType couponType = couponTypeRepository.selectById(coupon.getTypeId());

        CouponDetailDTO dto = new CouponDetailDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setName(coupon.getName());
        dto.setTypeName(couponType != null ? couponType.getDisplayName() : "");
        dto.setCarType(couponType != null ? couponType.getCarType() : "");
        dto.setServiceType(couponType != null ? couponType.getServiceType() : "");
        dto.setSettlementPrice(coupon.getSettlementPrice());
        dto.setStartTime(coupon.getStartTime());
        dto.setEndTime(coupon.getEndTime());
        dto.setStatus(coupon.getStatus());

        String qrToken = UUID.randomUUID().toString().replace("-", "");
        String cacheKey = "qrcode:" + qrToken;
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(couponId), qrCodeExpireMinutes, TimeUnit.MINUTES);
        
        String qrContent = qrToken;
        dto.setQrCodeUrl(qrCodeUtil.generateQrCodeBase64(qrContent, 300, 300));
        dto.setQrCodeExpireTime(System.currentTimeMillis() + qrCodeExpireMinutes * 60 * 1000);

        LambdaQueryWrapper<CouponStore> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CouponStore::getCouponId, couponId);
        List<CouponStore> couponStores = couponStoreRepository.selectList(csWrapper);
        
        List<StoreDTO> storeDTOs = new ArrayList<>();
        for (CouponStore cs : couponStores) {
            Store store = storeRepository.selectById(cs.getStoreId());
            if (store != null && store.getStatus() == 1) {
                StoreDTO storeDTO = new StoreDTO();
                storeDTO.setId(store.getId());
                storeDTO.setName(store.getName());
                storeDTO.setAddress(store.getAddress());
                storeDTO.setLongitude(store.getLongitude());
                storeDTO.setLatitude(store.getLatitude());
                storeDTO.setContactPhone(store.getContactPhone());
                storeDTO.setBusinessHours(store.getBusinessHours());
                storeDTOs.add(storeDTO);
            }
        }
        dto.setApplicableStores(storeDTOs);

        return dto;
    }

    @Override
    public String refreshQrCode(Long couponId, Long userId) {
        Coupon coupon = couponRepository.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("卡券不存在");
        }
        
        if (!coupon.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡券");
        }

        String qrToken = UUID.randomUUID().toString().replace("-", "");
        String cacheKey = "qrcode:" + qrToken;
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(couponId), qrCodeExpireMinutes, TimeUnit.MINUTES);
        
        return qrCodeUtil.generateQrCodeBase64(qrToken, 300, 300);
    }

    @Override
    public List<Coupon> importCoupons(List<Coupon> coupons, List<Long> storeIds) {
        List<Coupon> importedCoupons = new ArrayList<>();
        
        for (Coupon coupon : coupons) {
            coupon.setCode(generateCouponCode());
            coupon.setStatus(0);
            couponRepository.insert(coupon);
            
            for (Long storeId : storeIds) {
                CouponStore couponStore = new CouponStore();
                couponStore.setCouponId(coupon.getId());
                couponStore.setStoreId(storeId);
                couponStoreRepository.insert(couponStore);
            }
            
            importedCoupons.add(coupon);
        }
        
        return importedCoupons;
    }

    @Override
    public void distributeCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("卡券不存在");
        }
        
        if (coupon.getStatus() != 0) {
            throw new BusinessException("卡券已被发放");
        }
        
        coupon.setUserId(userId);
        coupon.setStatus(1);
        couponRepository.updateById(coupon);
    }

    private String generateCouponCode() {
        return "CW" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
