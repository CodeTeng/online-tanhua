package com.tanhua.server.controller;

import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:19
 */
@RestController
@RequestMapping("/messages")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    /**
     * 根据环信ID查询用户详细信息
     */
    @GetMapping("/userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        if (StringUtils.isBlank(huanxinId)) {
            return ResponseEntity.status(403).body("参数错误");
        }
        UserInfoVo vo = messagesService.findUserInfoByHuanxinId(huanxinId);
        return ResponseEntity.ok(vo);
    }
}
