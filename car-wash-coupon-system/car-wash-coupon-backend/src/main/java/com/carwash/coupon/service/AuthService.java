package com.carwash.coupon.service;

import com.carwash.coupon.dto.LoginResponse;

public interface AuthService {
    void sendSmsCode(String phone, String type);
    LoginResponse userLogin(String phone, String code);
    LoginResponse merchantLogin(String phone, String code);
    void acceptAgreement(Long userId);
}
