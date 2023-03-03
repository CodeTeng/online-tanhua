package com.tanhua.server.service;

import org.springframework.http.ResponseEntity;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 20:41
 */
public interface UserService {

    /**
     * 发送手机验证码
     *
     * @param mobile 手机号
     */
    ResponseEntity sendMsg(String mobile);

    /**
     * 用户登录
     *
     * @param mobile 手机号
     * @param code   验证码
     */
    ResponseEntity login(String mobile, String code);
}
