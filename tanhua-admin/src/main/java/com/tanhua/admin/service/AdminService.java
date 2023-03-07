package com.tanhua.admin.service;

import com.tanhua.model.vo.AdminVo;

import java.util.Map;

public interface AdminService {
    /**
     * 用户登录
     */
    Map login(String username, String password, String verificationCode, String uuid);

    /**
     * 获取用户的信息
     */
    AdminVo profile();
}
