package com.carwash.coupon.controller;

import com.carwash.coupon.dto.ApiResponse;
import com.carwash.coupon.dto.LoginResponse;
import com.carwash.coupon.dto.SendSmsCodeRequest;
import com.carwash.coupon.dto.UserLoginRequest;
import com.carwash.coupon.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "用户和商家认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public ApiResponse<Void> sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        authService.sendSmsCode(request.getPhone(), request.getType());
        return ApiResponse.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/user/login")
    public ApiResponse<LoginResponse> userLogin(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse response = authService.userLogin(request.getPhone(), request.getCode());
        return ApiResponse.success(response);
    }

    @Operation(summary = "商家登录")
    @PostMapping("/merchant/login")
    public ApiResponse<LoginResponse> merchantLogin(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse response = authService.merchantLogin(request.getPhone(), request.getCode());
        return ApiResponse.success(response);
    }

    @Operation(summary = "接受用户协议")
    @PostMapping("/agreement/accept")
    public ApiResponse<Void> acceptAgreement(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.acceptAgreement(userId);
        return ApiResponse.success();
    }
}
