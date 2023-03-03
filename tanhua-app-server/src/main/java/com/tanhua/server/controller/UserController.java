package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:10
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo) {
        // 1. 判断 token 是否合法 获取用户 id
        Long userId = UserHolder.getUserId();
        // 2. 向 userinfo 中设置用户id
        userInfo.setId(userId);
        // 3. 保存用户详细信息
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) {
        // 1. 判断 token 是否合法 获取用户 id
        Long userId = UserHolder.getUserId();
        // 2. 上传用户头像
        userInfoService.updateHead(headPhoto, userId);
        return ResponseEntity.ok(null);
    }
}
