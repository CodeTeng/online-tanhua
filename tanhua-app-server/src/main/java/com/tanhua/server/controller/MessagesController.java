package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            return ResponseEntity.status(400).body("请求参数错误");
        }
        UserInfoVo vo = messagesService.findUserInfoByHuanxinId(huanxinId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加好友
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody Map map) {
        Long friendId = Long.valueOf(map.get("userId").toString());
        messagesService.contacts(friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询联系人列表
     */
    @GetMapping("/contacts")
    public ResponseEntity contacts(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   String keyword) {
        PageResult pr = messagesService.findFriends(page, pagesize, keyword);
        return ResponseEntity.ok(pr);
    }
}
