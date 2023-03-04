package com.tanhua.server.service;

import com.tanhua.model.vo.PageResult;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/3 14:35
 */
public interface CommentsService {
    /**
     * 分页查询评论列表
     */
    PageResult findComments(String movementId, Integer page, Integer pagesize);

    /**
     * 发布评论
     */
    void saveComments(String movementId, String content);
}
