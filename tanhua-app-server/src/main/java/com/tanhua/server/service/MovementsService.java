package com.tanhua.server.service;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:03
 */
public interface MovementsService {
    /**
     * 发布文章
     */
    void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException;

    /**
     * 查询我的动态
     */
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}
