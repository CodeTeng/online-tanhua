package com.tanhua.dubbo.api;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 13:56
 */
public interface UserLikeApi {
    /**
     * 保存或者更新
     * 左滑右滑(喜欢/不喜欢)
     */
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike);
}
