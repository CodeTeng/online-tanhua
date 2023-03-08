package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManagerService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/7 20:28
 */
@RestController
@RequestMapping("/manage")
public class ManageController {
    @Autowired
    private ManagerService managerService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = managerService.findAllUsers(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据id查询用户详情
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findUserById(@PathVariable("userId") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(400).body("请求参数错误");
        }
        UserInfo userInfo = managerService.findUserById(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 分页查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid) {
        if (uid == null || uid <= 0) {
            return ResponseEntity.status(400).body("请求参数错误");
        }
        PageResult result = managerService.findAllVideos(page, pagesize, uid);
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询指定用户发布的所有动态
     */
    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid, Integer state) {
        if (uid == null || uid <= 0) {
            return ResponseEntity.status(400).body("请求参数错误");
        }
        PageResult result = managerService.findAllMovements(page, pagesize, uid, state);
        return ResponseEntity.ok(result);
    }

    /**
     * 用户冻结
     */
    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map params) {
        Map map = managerService.userFreeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户解冻
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map params) {
        Map map = managerService.userUnfreeze(params);
        return ResponseEntity.ok(map);
    }
}
