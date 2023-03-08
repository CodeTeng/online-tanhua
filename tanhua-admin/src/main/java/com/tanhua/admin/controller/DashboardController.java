package com.tanhua.admin.controller;

import com.tanhua.admin.service.AnalysisService;
import com.tanhua.model.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 15:11
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private AnalysisService analysisService;

    /**
     * 概要统计信息
     */
    @GetMapping("/summary")
    public ResponseEntity getSummary() {
        AnalysisSummaryVo vo = analysisService.summary();
        return ResponseEntity.ok(vo);
    }
}
