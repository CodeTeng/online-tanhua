package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/3 14:34
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    /**
     * 分页查询评论列表
     */
    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String movementId) {
        if (StringUtils.isBlank(movementId)) {
            return ResponseEntity.status(404).body("请求路径错误");
        }
        PageResult pr = commentsService.findComments(movementId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 发布评论
     */
    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map map) {
        String movementId = (String) map.get("movementId");
        String content = (String) map.get("comment");
        if (StringUtils.isAnyBlank(movementId, content)) {
            return ResponseEntity.status(403).body("请求参数错误");
        }
        commentsService.saveComments(movementId, content);
        return ResponseEntity.ok(null);
    }
}
