package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.service.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:16
 */
@RestController
@RequestMapping("/huanxin")
public class HuanXinController {
    @Autowired
    private HuanXinService huanXinService;

    /**
     * 查询当前用户的环信账号
     */
    @GetMapping("/user")
    public ResponseEntity user() {
        HuanXinUserVo vo = huanXinService.findHuanXinUser();
        return ResponseEntity.ok(vo);
    }
}
