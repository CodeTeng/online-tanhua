package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:09
 */
@RestController
@RequestMapping("/users")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    /**
     * 查询用户通用设置
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 设置陌生人问题
     */
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map map) {
        // 获取参数
        String content = (String) map.get("content");
        if (StringUtils.isBlank(content)) {
            return ResponseEntity.status(403).body("问题不能为空");
        }
        settingsService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    /**
     * 通知设置
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map) {
        // 获取参数
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询黑名单列表
     */
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        // 1、调用service查询
        PageResult pr = settingsService.blacklist(page, size);
        // 2、构造返回
        return ResponseEntity.ok(pr);
    }

    /**
     * 取消黑名单
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid") Long blackUserId) {
        if (blackUserId == null || blackUserId <= 0) {
            return ResponseEntity.status(404).body("请求路径错误");
        }
        settingsService.deleteBlackList(blackUserId);
        return ResponseEntity.ok(null);
    }
}
