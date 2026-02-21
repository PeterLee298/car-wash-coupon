package com.carwash.coupon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class VerifyCouponRequest {
    @NotNull(message = "核销记录ID不能为空")
    private Long verificationId;
    
    @NotNull(message = "照片不能为空")
    @Min(value = 1, message = "至少上传一张照片")
    private List<String> photos;
}
