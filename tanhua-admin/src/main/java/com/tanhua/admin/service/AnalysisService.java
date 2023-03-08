package com.tanhua.admin.service;

import com.tanhua.model.vo.AnalysisSummaryVo;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 14:42
 */
public interface AnalysisService {
    /**
     * 定时统计日志数据到统计表中
     */
    void analysis();

    /**
     * 首页-概要统计信息
     */
    AnalysisSummaryVo summary();
}
