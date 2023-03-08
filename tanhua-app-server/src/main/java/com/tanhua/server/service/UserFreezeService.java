package com.tanhua.server.service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 9:59
 */
public interface UserFreezeService {
    /**
     * 判断用户是否被冻结
     */
    void checkUserStatus(String state, Long userId);
}
