package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:12
 */
public interface SettingsApi {
    /**
     * 根据用户id查询通用设置
     *
     * @param userId 用户id
     * @return 通用设置
     */
    Settings findByUserId(Long userId);

    /**
     * 添加设置
     *
     * @param settings 通用设置
     */
    void save(Settings settings);

    /**
     * 更新通用设置
     *
     * @param settings 通用设置
     */
    void update(Settings settings);
}
