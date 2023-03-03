package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:02
 */
public interface MovementApi {
    /**
     * 发布文章
     */
    void publish(Movement movement);

    /**
     * 分页查询个人动态
     */
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}
