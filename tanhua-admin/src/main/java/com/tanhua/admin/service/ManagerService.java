package com.tanhua.admin.service;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/7 20:30
 */
public interface ManagerService {
    /**
     * 分页查询用户列表
     */
    PageResult findAllUsers(Integer page, Integer pagesize);

    /**
     * 根据id查询用户详情
     */
    UserInfo findUserById(Long userId);

    /**
     * 分页查询指定用户发布的所有视频列表
     */
    PageResult findAllVideos(Integer page, Integer pagesize, Long uid);

    /**
     * 分页查询指定用户发布的所有动态
     */
    PageResult findAllMovements(Integer page, Integer pagesize, Long userId, Integer state);

    /**
     * 用户冻结
     */
    Map userFreeze(Map params);

    /**
     * 用户解冻
     */
    Map userUnfreeze(Map params);
}
