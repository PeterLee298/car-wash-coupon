package com.carwash.coupon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EvaluationRequest {
    @NotNull(message = "核销记录ID不能为空")
    private Long verificationRecordId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;
    
    private String content;
    
    private List<String> photos;

    // Getter and Setter methods
    public Long getVerificationRecordId() {
        return verificationRecordId;
    }

    public void setVerificationRecordId(Long verificationRecordId) {
        this.verificationRecordId = verificationRecordId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
