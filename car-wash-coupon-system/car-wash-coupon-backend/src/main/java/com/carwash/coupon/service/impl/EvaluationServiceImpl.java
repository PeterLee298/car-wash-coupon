package com.carwash.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carwash.coupon.dto.EvaluationRequest;
import com.carwash.coupon.entity.Evaluation;
import com.carwash.coupon.entity.VerificationRecord;
import com.carwash.coupon.exception.BusinessException;
import com.carwash.coupon.repository.EvaluationRepository;
import com.carwash.coupon.repository.VerificationRecordRepository;
import com.carwash.coupon.service.EvaluationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final VerificationRecordRepository verificationRecordRepository;
    private final ObjectMapper objectMapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository,
                                VerificationRecordRepository verificationRecordRepository,
                                ObjectMapper objectMapper) {
        this.evaluationRepository = evaluationRepository;
        this.verificationRecordRepository = verificationRecordRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void submitEvaluation(EvaluationRequest request, Long userId) {
        VerificationRecord record = verificationRecordRepository.selectById(request.getVerificationRecordId());
        if (record == null) {
            throw new BusinessException("核销记录不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权评价此核销记录");
        }

        if (record.getStatus() != 2) {
            throw new BusinessException("核销记录未通过审核，无法评价");
        }

        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getVerificationRecordId, request.getVerificationRecordId());
        if (evaluationRepository.selectCount(wrapper) > 0) {
            throw new BusinessException("已评价过，请勿重复评价");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setUserId(userId);
        evaluation.setVerificationRecordId(request.getVerificationRecordId());
        evaluation.setStoreId(record.getStoreId());
        evaluation.setRating(request.getRating());
        evaluation.setContent(request.getContent());
        
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                evaluation.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("照片数据序列化失败");
            }
        }
        
        evaluationRepository.insert(evaluation);
    }

    @Override
    public Evaluation getEvaluationByVerificationId(Long verificationRecordId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getVerificationRecordId, verificationRecordId);
        return evaluationRepository.selectOne(wrapper);
    }
}
