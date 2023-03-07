package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:18
 */
public interface UserInfoApi {
    /**
     * 保存用户详细信息
     *
     * @param userInfo 用户详细信息
     */
    void save(UserInfo userInfo);

    /**
     * 根据用户ID查询用户信息
     */
    UserInfo findById(Long id);

    /**
     * 更新用户信息
     */
    void update(UserInfo userInfo);

    /**
     * 批量查询用户信息
     *
     * @param userIds  用户id集合
     * @param userInfo 用户查询条件
     */
    Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo userInfo);

    /**
     * 分页查询所有用户信息
     */
    IPage<UserInfo> findAll(Integer page, Integer pagesize);
}
