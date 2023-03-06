package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:23
 */
@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {
    @Autowired
    private SmallVideosService smallVideosService;

    /**
     * 发布视频
     *
     * @param videoThumbnail 封面图
     * @param videoFile      视频文件
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) {
        smallVideosService.saveVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频列表
     */
    @GetMapping
    public ResponseEntity queryVideoList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = smallVideosService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(result);
    }
}
