package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 21:13
 */
public interface UserApi {
    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    User findByMobile(String mobile);

    /**
     * 保存用户
     *
     * @param user 待保存用户
     * @return 用户id
     */
    Long save(User user);

    /**
     * 更新用户信息
     */
    void update(User user);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 根据id查询用户
     */
    User findById(Long userId);

    /**
     * 根据环信id查询用户信息
     */
    User findByHuanxinId(String huanxinId);
}
