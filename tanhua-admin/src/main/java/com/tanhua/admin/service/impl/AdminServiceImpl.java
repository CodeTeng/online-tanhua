package com.tanhua.admin.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.admin.Admin;
import com.tanhua.model.vo.AdminVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/7 19:04
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map login(String username, String password, String verificationCode, String uuid) {
        String key = Constants.CAP_CODE + uuid;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(code) || !verificationCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        // 删除验证码
        stringRedisTemplate.delete(key);
        // 根据用户名查询用户
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        password = SecureUtil.md5(password);
        if (admin == null || !password.equals(admin.getPassword())) {
            throw new BusinessException("用户名或者密码");
        }
        // 通过JWT生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("username", username);
        String token = JwtUtils.getToken(claims);
        // 返回
        Map res = new HashMap();
        res.put("token", token);
        return res;
    }

    @Override
    public AdminVo profile() {
        Long id = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(id);
        return AdminVo.init(admin);
    }
}
