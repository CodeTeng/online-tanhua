package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 22:41
 */
public interface FocusUserApi {
    /**
     * 判断是否关注过视频作者
     */
    Boolean hasFocus(Long uid, Long userId);

    /**
     * 保存到MongoDB中
     */
    void save(FocusUser focusUser);

    /**
     * 从MongoDB中删除
     */
    void delete(FocusUser focusUser);
}
