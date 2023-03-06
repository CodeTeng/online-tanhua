package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 19:16
 */
public interface VisitorsApi {
    /**
     * 保存访客数据
     */
    void save(Visitors visitors);

    /**
     * 查询访客列表
     *
     * @param date   日期
     * @param userId 用户id
     */
    List<Visitors> queryMyVisitors(Long date, Long userId);
}
