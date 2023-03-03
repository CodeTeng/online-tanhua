package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @description: 登录控制器
 * @author: ~Teng~
 * @date: 2023/3/1 20:40
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 获取手机验证码
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        // 1、获取手机号码
        String mobile = (String) map.get("phone");
        if (StringUtils.isBlank(mobile)) {
            return ResponseEntity.status(500).body("请输入手机号");
        }
        // 2、调用service发送短信
        return userService.sendMsg(mobile);
    }

    /**
     * 手机登录
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        String mobile = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        if (StringUtils.isAnyBlank(mobile)) {
            return ResponseEntity.status(500).body("请输入手机号");
        }
        if (StringUtils.isBlank(code)) {
            return ResponseEntity.status(500).body("请输入验证码");
        }
        return userService.login(mobile, code);
    }
}
