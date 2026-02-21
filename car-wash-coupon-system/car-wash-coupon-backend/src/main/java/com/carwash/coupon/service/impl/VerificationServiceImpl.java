package com.carwash.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carwash.coupon.entity.Coupon;
import com.carwash.coupon.entity.CouponStore;
import com.carwash.coupon.entity.CouponType;
import com.carwash.coupon.entity.MerchantAccount;
import com.carwash.coupon.entity.Store;
import com.carwash.coupon.entity.VerificationRecord;
import com.carwash.coupon.exception.BusinessException;
import com.carwash.coupon.repository.*;
import com.carwash.coupon.service.VerificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRecordRepository verificationRecordRepository;
    private final CouponRepository couponRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final CouponStoreRepository couponStoreRepository;
    private final MerchantAccountRepository merchantAccountRepository;
    private final StoreRepository storeRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    public VerificationServiceImpl(VerificationRecordRepository verificationRecordRepository,
                                  CouponRepository couponRepository, CouponTypeRepository couponTypeRepository,
                                  CouponStoreRepository couponStoreRepository,
                                  MerchantAccountRepository merchantAccountRepository,
                                  StoreRepository storeRepository,
                                  StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.verificationRecordRepository = verificationRecordRepository;
        this.couponRepository = couponRepository;
        this.couponTypeRepository = couponTypeRepository;
        this.couponStoreRepository = couponStoreRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.storeRepository = storeRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Override
    public Map<String, Object> verifyCoupon(String couponCode, Long merchantAccountId) {
        String cacheKey = "qrcode:" + couponCode;
        String couponIdStr = redisTemplate.opsForValue().get(cacheKey);
        
        Coupon coupon;
        if (couponIdStr != null) {
            coupon = couponRepository.selectById(Long.parseLong(couponIdStr));
        } else {
            LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Coupon::getCode, couponCode);
            coupon = couponRepository.selectOne(wrapper);
        }
        
        if (coupon == null) {
            throw new BusinessException("券码不可用，请重新扫描");
        }

        if (coupon.getStatus() == 3) {
            throw new BusinessException("券码已过期，请提示客户刷新券码");
        }

        if (coupon.getStatus() == 2) {
            throw new BusinessException("券码已使用");
        }

        if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("券码已过期，请提示客户刷新券码");
        }

        MerchantAccount merchant = merchantAccountRepository.selectById(merchantAccountId);
        if (merchant == null) {
            throw new BusinessException("商家账号不存在");
        }

        LambdaQueryWrapper<CouponStore> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CouponStore::getCouponId, coupon.getId())
                 .eq(CouponStore::getStoreId, merchant.getStoreId());
        CouponStore couponStore = couponStoreRepository.selectOne(csWrapper);
        
        if (couponStore == null) {
            throw new BusinessException("该券不适用于当前门店");
        }

        Store store = storeRepository.selectById(merchant.getStoreId());
        if (store == null) {
            throw new BusinessException("门店信息不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("coupon", coupon);
        result.put("store", store);
        return result;
    }

    @Override
    public String uploadPhoto(MultipartFile file, String couponCode, Long merchantAccountId) {
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的照片");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        
        // 按日期创建目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File dateDir = new File(uploadPath + File.separator + datePath);
        if (!dateDir.exists()) {
            dateDir.mkdirs();
        }

        // 保存文件
        File dest = new File(dateDir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("照片上传失败");
        }

        // 返回相对路径，用于存储和访问
        return "/uploads/" + datePath + "/" + filename;
    }

    @Override
    public Long submitVerify(String couponCode, List<String> photoUrls, Long merchantAccountId) {
        // 根据券码查询优惠券
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getCode, couponCode);
        Coupon coupon = couponRepository.selectOne(wrapper);
        
        if (coupon == null) {
            throw new BusinessException("卡券不存在");
        }

        if (coupon.getStatus() != 1) {
            throw new BusinessException("卡券状态异常，无法核销");
        }

        // 获取商家信息
        MerchantAccount merchant = merchantAccountRepository.selectById(merchantAccountId);
        if (merchant == null) {
            throw new BusinessException("商家账号不存在");
        }

        // 检查照片数量
        CouponType couponType = couponTypeRepository.selectById(coupon.getTypeId());
        if (couponType == null) {
            throw new BusinessException("卡券类型不存在");
        }

        if (photoUrls == null || photoUrls.size() < couponType.getMinPhotos()) {
            throw new BusinessException("照片数量不足，至少需要上传" + couponType.getMinPhotos() + "张照片");
        }

        // 更新卡券状态
        coupon.setStatus(2);
        couponRepository.updateById(coupon);

        // 序列化照片URL
        String photosJson;
        try {
            photosJson = objectMapper.writeValueAsString(photoUrls);
        } catch (JsonProcessingException e) {
            throw new BusinessException("照片数据序列化失败");
        }

        // 创建核销记录
        VerificationRecord record = new VerificationRecord();
        record.setCouponId(coupon.getId());
        record.setCouponCode(coupon.getCode());
        record.setUserId(coupon.getUserId());
        record.setStoreId(merchant.getStoreId());
        record.setMerchantAccountId(merchantAccountId);
        record.setStatus(1); // 已提交，待审核
        record.setPhotos(photosJson);
        record.setVerifyTime(LocalDateTime.now());
        record.setCreatedTime(LocalDateTime.now());
        verificationRecordRepository.insert(record);

        return record.getId();
    }

    @Override
    public Map<String, Object> getVerificationRecords(Long merchantAccountId, String month, Integer page, Integer pageSize) {
        // 获取商家信息
        MerchantAccount merchant = merchantAccountRepository.selectById(merchantAccountId);
        if (merchant == null) {
            throw new BusinessException("商家账号不存在");
        }

        // 构建查询条件
        LambdaQueryWrapper<VerificationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerificationRecord::getStoreId, merchant.getStoreId());
        
        if (month != null && !month.isEmpty()) {
            wrapper.apply("DATE_FORMAT(created_time, '%Y-%m') = {0}", month);
        }
        
        wrapper.orderByDesc(VerificationRecord::getCreatedTime);
        
        // 计算总数
        int total = verificationRecordRepository.selectCount(wrapper).intValue();
        
        // 分页查询
        int offset = (page - 1) * pageSize;
        List<VerificationRecord> records = verificationRecordRepository.selectList(
                wrapper.last("LIMIT " + offset + ", " + pageSize)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);
        
        return result;
    }

    @Override
    public VerificationRecord getVerificationDetail(Long id) {
        return verificationRecordRepository.selectById(id);
    }

    @Override
    public void auditVerification(Long id, Integer status, String remark, Long auditorId) {
        VerificationRecord record = verificationRecordRepository.selectById(id);
        if (record == null) {
            throw new BusinessException("核销记录不存在");
        }

        record.setStatus(status);
        record.setAuditRemark(remark);
        record.setAuditorId(auditorId);
        record.setAuditTime(LocalDateTime.now());
        verificationRecordRepository.updateById(record);
    }
}
