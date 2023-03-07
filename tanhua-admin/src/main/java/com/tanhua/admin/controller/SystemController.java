package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.AdminVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成图片验证码
     */
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        // 1. 通过工具类生成验证码对象（图片数据和验证码信息）
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = lineCaptcha.getCode();
        // 2. 将验证码存入到redis
        stringRedisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, code);
        // 3. 通过输出流输出验证码
        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");
        if (StringUtils.isAnyBlank(username, password)) {
            Map errorMap = new HashMap();
            errorMap.put("message", "用户名或者密码为空");
            return ResponseEntity.status(500).body(errorMap);
        }
        if (StringUtils.isBlank(verificationCode)) {
            Map errorMap = new HashMap();
            errorMap.put("message", "验证码为空");
            return ResponseEntity.status(500).body(errorMap);
        }
        if (StringUtils.isBlank(uuid)) {
            return ResponseEntity.status(500).body("参数错误");
        }
        Map retMap = adminService.login(username, password, verificationCode, uuid);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取用户的信息
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = adminService.profile();
        return ResponseEntity.ok(vo);
    }
}
