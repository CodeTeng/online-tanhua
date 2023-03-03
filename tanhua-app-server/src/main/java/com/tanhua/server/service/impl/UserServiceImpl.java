package com.tanhua.server.service.impl;

import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 20:44
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private UserApi userApi;

    @Override
    public ResponseEntity sendMsg(String mobile) {
        // 1. 随机生成6位号码
        String code = RandomStringUtils.randomNumeric(6);
        // 2. 发送短信
        smsTemplate.sendSms(mobile, code);
        // 3. 存入 redis
        stringRedisTemplate.opsForValue().set(Constants.SMS_CODE + mobile, code, 5L, TimeUnit.MINUTES);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity login(String mobile, String code) {
        // 1. 从 redis 中获取验证码
        String redisCode = stringRedisTemplate.opsForValue().get(Constants.SMS_CODE + mobile);
        // 2. 判断是否有效
        if (StringUtils.isBlank(redisCode)) {
            return ResponseEntity.status(500).body("验证码已过期");
        }
        // 3. 进行判断用户输入验证码和获取验证码
        if (!redisCode.equals(code)) {
            return ResponseEntity.status(500).body("输入验证码错误");
        }
        // 4. 删除验证码
        stringRedisTemplate.delete(Constants.SMS_CODE + mobile);
        // 5. 根据手机号查询用户信息
        User user = userApi.findByMobile(mobile);
        boolean isNew = false;
        if (user == null) {
            // 用户不存在 创建新用户
            user = new User();
            user.setMobile(mobile);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
        }
        // 6. 生成 token
        Map tokenMap = new HashMap();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", mobile);
        String token = JwtUtils.getToken(tokenMap);
        // 7. 构造返回值
        Map resMap = new HashMap();
        resMap.put("token", token);
        resMap.put("isNew", isNew);
        return ResponseEntity.ok(resMap);
    }
}
