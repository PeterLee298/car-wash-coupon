package com.carwash.coupon.service;

import com.carwash.coupon.entity.Coupon;
import com.carwash.coupon.entity.VerificationRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VerificationService {
    Map<String, Object> verifyCoupon(String couponCode, Long merchantAccountId);
    String uploadPhoto(MultipartFile file, String couponCode, Long merchantAccountId);
    Long submitVerify(String couponCode, List<String> photoUrls, Long merchantAccountId);
    Map<String, Object> getVerificationRecords(Long merchantAccountId, String month, Integer page, Integer pageSize);
    VerificationRecord getVerificationDetail(Long id);
    void auditVerification(Long id, Integer status, String remark, Long auditorId);
}
