package com.tanhua.server.service;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:10
 */
public interface SettingsService {
    /**
     * 查询用户通用设置
     */
    SettingsVo settings();

    /**
     * 设置陌生人问题
     *
     * @param content 问题
     */
    void saveQuestion(String content);

    /**
     * 通知设置
     */
    void saveSettings(Map map);

    /**
     * 分页查询黑名单列表
     */
    PageResult blacklist(int page, int size);

    /**
     * 移除黑名单
     *
     * @param blackUserId 黑名单id
     */
    void deleteBlackList(Long blackUserId);
}
