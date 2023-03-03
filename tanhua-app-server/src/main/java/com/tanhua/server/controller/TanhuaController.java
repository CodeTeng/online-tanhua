package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 15:41
 */
@RestController
@RequestMapping("/tanhua")
public class TanhuaController {
    @Autowired
    private TanhuaService tanhuaService;

    /**
     * 查询今日佳人
     */
    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        TodayBest todayBest = tanhuaService.todayBest();
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 查询分页推荐好友列表
     */
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto dto) {
        PageResult pr = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(pr);
    }
}
