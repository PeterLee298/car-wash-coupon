package com.carwash.coupon.dto;

import jakarta.validation.constraints.NotBlank;

public class SendSmsCodeRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    private String type;

    // Getter and Setter methods
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
