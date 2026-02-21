package com.carwash.coupon.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carwash.coupon.dto.LoginResponse;
import com.carwash.coupon.entity.MerchantAccount;
import com.carwash.coupon.entity.SmsCode;
import com.carwash.coupon.entity.User;
import com.carwash.coupon.exception.BusinessException;
import com.carwash.coupon.repository.MerchantAccountRepository;
import com.carwash.coupon.repository.SmsCodeRepository;
import com.carwash.coupon.repository.UserRepository;
import com.carwash.coupon.service.AuthService;
import com.carwash.coupon.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final MerchantAccountRepository merchantAccountRepository;
    private final SmsCodeRepository smsCodeRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    public AuthServiceImpl(UserRepository userRepository, MerchantAccountRepository merchantAccountRepository,
                          SmsCodeRepository smsCodeRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.smsCodeRepository = smsCodeRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendSmsCode(String phone, String type) {
        String cacheKey = "sms:" + type + ":" + phone;
        String lastSendTime = redisTemplate.opsForValue().get(cacheKey);
        if (lastSendTime != null) {
            throw new BusinessException(400, "验证码发送过于频繁，请稍后再试");
        }

        String code = String.format("%06d", new Random().nextInt(1000000));

        try {
            Client client = createSmsClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            if (!"OK".equals(response.getBody().getCode())) {
                throw new BusinessException("短信发送失败：" + response.getBody().getMessage());
            }
        } catch (Exception e) {
            throw new BusinessException("短信发送失败：" + e.getMessage());
        }

        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(3));
        smsCodeRepository.insert(smsCode);

        redisTemplate.opsForValue().set(cacheKey, String.valueOf(System.currentTimeMillis()), 60, TimeUnit.SECONDS);
    }

    @Override
    public LoginResponse userLogin(String phone, String code) {
        validateSmsCode(phone, code, "user_login");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userRepository.selectOne(wrapper);

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            user.setAgreementAccepted(0);
            userRepository.insert(user);
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        markSmsCodeUsed(phone, "user_login");

        String token = jwtUtil.generateToken(user.getId(), "user");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        return response;
    }

    @Override
    public LoginResponse merchantLogin(String phone, String code) {
        validateSmsCode(phone, code, "merchant_login");

        LambdaQueryWrapper<MerchantAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantAccount::getPhone, phone);
        MerchantAccount merchant = merchantAccountRepository.selectOne(wrapper);

        if (merchant == null) {
            throw new BusinessException("该手机号未注册为商家账号");
        }

        if (merchant.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        markSmsCodeUsed(phone, "merchant_login");

        String token = jwtUtil.generateToken(merchant.getId(), "merchant");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(merchant.getId());
        response.setPhone(merchant.getPhone());
        response.setNickname(merchant.getName());
        return response;
    }

    @Override
    public void acceptAgreement(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setAgreementAccepted(1);
        userRepository.updateById(user);
    }

    private void validateSmsCode(String phone, String code, String type) {
        LambdaQueryWrapper<SmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCode::getPhone, phone)
               .eq(SmsCode::getCode, code)
               .eq(SmsCode::getType, type)
               .eq(SmsCode::getUsed, 0)
               .gt(SmsCode::getExpireTime, LocalDateTime.now())
               .orderByDesc(SmsCode::getCreatedTime)
               .last("LIMIT 1");
        
        SmsCode smsCode = smsCodeRepository.selectOne(wrapper);
        if (smsCode == null) {
            throw new BusinessException("验证码错误或已过期");
        }
    }

    private void markSmsCodeUsed(String phone, String type) {
        LambdaQueryWrapper<SmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCode::getPhone, phone)
               .eq(SmsCode::getType, type)
               .eq(SmsCode::getUsed, 0);
        
        SmsCode smsCode = new SmsCode();
        smsCode.setUsed(1);
        smsCodeRepository.update(smsCode, wrapper);
    }

    private Client createSmsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        return new Client(config);
    }
}
