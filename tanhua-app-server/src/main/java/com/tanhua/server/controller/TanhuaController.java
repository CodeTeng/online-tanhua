package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(403).body("参数错误");
        }
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }

    /**
     * 查看陌生人问题
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(403).body("参数错误");
        }
        String questions = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 回复陌生人问题
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {
        // 前端传递的userId:是Integer类型的
        String obj = String.valueOf(map.get("userId"));
        Long userId = Long.valueOf(obj);
        // 回复内容
        String reply = map.get("reply").toString();
        if (StringUtils.isBlank(reply)) {
            return ResponseEntity.status(403).body("回复内容不能为空");
        }
        tanhuaService.replyQuestions(userId, reply);
        return ResponseEntity.ok(null);
    }

    /**
     * 探花-推荐用户列表
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBest> list = tanhuaService.queryCardsList();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */
    @GetMapping("{id}/love")
    public ResponseEntity likeUser(@PathVariable("id") Long likeUserId) {
        if (likeUserId == null || likeUserId <= 0) {
            return ResponseEntity.status(403).body("参数错误");
        }
        tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity notLikeUser(@PathVariable("id") Long likeUserId) {
        if (likeUserId == null || likeUserId <= 0) {
            return ResponseEntity.status(403).body("参数错误");
        }
        tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender,
                                                          @RequestParam(defaultValue = "5000") String distance) {
        List<NearUserVo> list = tanhuaService.queryNearUser(gender, distance);
        return ResponseEntity.ok(list);
    }
}
