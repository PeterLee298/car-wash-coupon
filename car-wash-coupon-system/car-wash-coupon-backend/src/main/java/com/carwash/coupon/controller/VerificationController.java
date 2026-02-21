package com.carwash.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.dto.EvaluationRequest;
import com.carwash.coupon.entity.Coupon;
import com.carwash.coupon.entity.Store;
import com.carwash.coupon.entity.VerificationRecord;
import com.carwash.coupon.service.EvaluationService;
import com.carwash.coupon.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "核销接口", description = "商家核销相关接口")
@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private final VerificationService verificationService;
    private final EvaluationService evaluationService;

    public VerificationController(VerificationService verificationService, EvaluationService evaluationService) {
        this.verificationService = verificationService;
        this.evaluationService = evaluationService;
    }

    @Operation(summary = "验证卡券码")
    @PostMapping("/verify-coupon")
    public ApiResponse<Map<String, Object>> verifyCoupon(
            @RequestBody Map<String, String> params,
            HttpServletRequest request) {
        String couponCode = params.get("couponCode");
        Long merchantAccountId = (Long) request.getAttribute("userId");
        Map<String, Object> result = verificationService.verifyCoupon(couponCode, merchantAccountId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "上传施工照片")
    @PostMapping("/upload-photo")
    public ApiResponse<String> uploadPhoto(
            @RequestParam("photo") MultipartFile file,
            @RequestParam String couponCode,
            HttpServletRequest request) {
        Long merchantAccountId = (Long) request.getAttribute("userId");
        String photoUrl = verificationService.uploadPhoto(file, couponCode, merchantAccountId);
        return ApiResponse.success(photoUrl);
    }

    @Operation(summary = "提交核销")
    @PostMapping("/submit-verify")
    public ApiResponse<Long> submitVerify(
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        String couponCode = (String) params.get("couponCode");
        List<String> photoUrls = new ArrayList<>();
        Object photoUrlsObj = params.get("photoUrls");
        if (photoUrlsObj instanceof List) {
            for (Object url : (List<?>) photoUrlsObj) {
                if (url instanceof String) {
                    photoUrls.add((String) url);
                }
            }
        }
        Long merchantAccountId = (Long) request.getAttribute("userId");
        Long verificationId = verificationService.submitVerify(couponCode, photoUrls, merchantAccountId);
        return ApiResponse.success(verificationId);
    }

    @Operation(summary = "获取核销记录列表")
    @GetMapping("/records")
    public ApiResponse<Map<String, Object>> getVerificationRecords(
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long merchantAccountId = (Long) request.getAttribute("userId");
        Map<String, Object> result = verificationService.getVerificationRecords(merchantAccountId, month, page, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "获取核销记录详情")
    @GetMapping("/record/{id}")
    public ApiResponse<VerificationRecord> getVerificationDetail(@PathVariable Long id) {
        VerificationRecord record = verificationService.getVerificationDetail(id);
        return ApiResponse.success(record);
    }

    @Operation(summary = "提交评价")
    @PostMapping("/evaluation")
    public ApiResponse<Void> submitEvaluation(
            @RequestBody EvaluationRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        evaluationService.submitEvaluation(request, userId);
        return ApiResponse.success();
    }
}
