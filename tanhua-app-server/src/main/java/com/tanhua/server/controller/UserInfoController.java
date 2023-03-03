package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 9:59
 */
@RestController
@RequestMapping("/users")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping
    public ResponseEntity getUsers(Long userId) {
        if (userId == null) {
            // 查询当前用户
            userId = UserHolder.getUserId();
        }
        UserInfoVo userInfoVo = userInfoService.findById(userId);
        return ResponseEntity.ok(userInfoVo);
    }

    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo) {
        // 1. 判断 token 是否合法 获取用户 id
        Long userId = UserHolder.getUserId();
        // 设置id
        userInfo.setId(userId);
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }
}
