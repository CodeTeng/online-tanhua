package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/3 14:37
 */
public interface CommentApi {
    /**
     * 分页查询
     */
    List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize);

    /**
     * 发布评论并获取评论数量
     */
    Integer save(Comment comment);
}
