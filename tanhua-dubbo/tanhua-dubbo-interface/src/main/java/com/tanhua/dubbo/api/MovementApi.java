package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

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

    /**
     * 查询好友动态
     */
    List<Movement> findFriendMovements(Integer page, Integer pagesize, Long friendId);

    /**
     * 随机模拟10条数据
     *
     * @param counts 条数
     */
    List<Movement> randomMovements(Integer counts);

    /**
     * 根据pid数组查询动态
     */
    List<Movement> findMovementsByPids(List<Long> pidList);
}
