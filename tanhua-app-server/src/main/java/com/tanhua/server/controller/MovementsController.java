package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.service.CommentsService;
import com.tanhua.server.service.MovementsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:03
 */
@RestController
@RequestMapping("/movements")
public class MovementsController {
    @Autowired
    private MovementsService movementsService;
    @Autowired
    private CommentsService commentsService;

    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile[] imageContent) throws IOException {
        movementsService.publishMovement(movement, imageContent);
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询我的动态
     */
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findByUserId(userId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询好友动态
     */
    @GetMapping
    public ResponseEntity movements(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findFriendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询推荐动态列表
     */
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findRecommendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询单条动态
     */
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        MovementsVo vo = movementsService.findById(movementId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId) {
        if (StringUtils.isBlank(movementId)) {
            ResponseEntity.status(400).body("路径参数错误");
        }
        Integer likeCount = commentsService.likeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId) {
        if (StringUtils.isBlank(movementId)) {
            ResponseEntity.status(400).body("路径参数错误");
        }
        Integer likeCount = commentsService.dislikeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 喜欢动态
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.loveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消动态喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.disloveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 谁看过我
     */
    @GetMapping("/visitors")
    public ResponseEntity queryVisitorsList() {
        List<VisitorsVo> list = movementsService.queryVisitorsList();
        return ResponseEntity.ok(list);
    }
}
