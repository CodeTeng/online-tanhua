package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 13:25
 */
public interface BlackListApi {
    /**
     * 分页查询黑名单列表
     */
    IPage<UserInfo> findByUserId(Long userId, int page, int size);

    /**
     * 移除黑名单
     *
     * @param blackUserId 黑名单id
     */
    void deleteById(Long userId, Long blackUserId);
}
