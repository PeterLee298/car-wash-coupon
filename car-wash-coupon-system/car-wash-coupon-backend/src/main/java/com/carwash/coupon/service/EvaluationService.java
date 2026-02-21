package com.carwash.coupon.service;

import com.carwash.coupon.dto.EvaluationRequest;
import com.carwash.coupon.entity.Evaluation;

public interface EvaluationService {
    void submitEvaluation(EvaluationRequest request, Long userId);
    Evaluation getEvaluationByVerificationId(Long verificationRecordId);
}
