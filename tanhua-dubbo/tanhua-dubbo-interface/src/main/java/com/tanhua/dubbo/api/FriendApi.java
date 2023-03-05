package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 17:34
 */
public interface FriendApi {
    /**
     * 添加朋友
     */
    void save(Long userId, Long friendId);

    /**
     * 分页查询当前用户的所有好友
     */
    List<Friend> findByUserId(Long userId, Integer page, Integer pagesize);

    /**
     * 删除朋友
     */
    void delete(Long userId, Long friendId);
}
