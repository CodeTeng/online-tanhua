package com.tanhua.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.admin.service.AnalysisService;
import com.tanhua.model.admin.Analysis;
import com.tanhua.model.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 14:42
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;
    @Autowired
    private LogMapper logMapper;

    @Override
    public void analysis() {
        try {
            // 1、定义查询的日期
            String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String yesdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            // 2、统计数据-注册数量
            Integer regCount = logMapper.queryByTypeAndLogTime("0102", todayStr);
            // 3、统计数据-登录数量
            Integer loginCount = logMapper.queryByTypeAndLogTime("0101", todayStr);
            // 4、统计数据-活跃数量
            Integer activeCount = logMapper.queryByLogTime(todayStr);
            // 5、统计数据-次日留存
            Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yesdayStr);
            // 6、根据日期查询数据
            QueryWrapper<Analysis> qw = new QueryWrapper<Analysis>();
            qw.eq("record_date", new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
            // 7、构造Analysis对象
            Analysis analysis = analysisMapper.selectOne(qw);
            // 8、如果存在，更新，如果不存在保存
            if (analysis != null) {
                analysis.setNumRegistered(regCount);
                analysis.setNumLogin(loginCount);
                analysis.setNumActive(activeCount);
                analysis.setNumRetention1d(numRetention1d);
                analysisMapper.updateById(analysis);
            } else {
                analysis = new Analysis();
                analysis.setNumRegistered(regCount);
                analysis.setNumLogin(loginCount);
                analysis.setNumActive(activeCount);
                analysis.setNumRetention1d(numRetention1d);
                analysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
                analysis.setCreated(new Date());
                analysisMapper.insert(analysis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AnalysisSummaryVo summary() {
        Date date = new Date();
        String today = DateUtil.today();//今日时间 yyyy-MM-dd
        String yesterday = DateUtil.yesterday().toDateStr();//昨天时间
        AnalysisSummaryVo vo = new AnalysisSummaryVo();
        vo.setCumulativeUsers(analysisMapper.totalUser());//累计用户数
        vo.setActivePassMonth(analysisMapper.countActive(DateUtil.offsetDay(date, -30)));//过去30天活跃用户数
        vo.setActivePassWeek(analysisMapper.countActive(DateUtil.offsetDay(date, -7)));//过去7天活跃用户

        Long todayNewUsers = analysisMapper.countNewUsers(today);
        Long yesterdayNewUsers = analysisMapper.countNewUsers(yesterday);
        vo.setNewUsersToday(todayNewUsers);//今日新增用户数量
        vo.setNewUsersTodayRate(computeRate(todayNewUsers, yesterdayNewUsers));//今日新增用户涨跌率

        Long todayLoginTimes = analysisMapper.countLoginTimes(today);
        Long yesterdayLoginTimes = analysisMapper.countLoginTimes(yesterday);
        vo.setLoginTimesToday(todayLoginTimes);//今日登录次数
        vo.setLoginTimesTodayRate(computeRate(todayLoginTimes, yesterdayLoginTimes));//今日登录次数涨跌率

        Long todayActiveUsers = analysisMapper.countActiveUsers(today);
        Long yesterdayActiveUsers = analysisMapper.countActiveUsers(yesterday);
        vo.setActiveUsersToday(todayActiveUsers);//今日活跃用户数量
        vo.setActiveUsersTodayRate(computeRate(todayActiveUsers, yesterdayActiveUsers));//今日活跃用户涨跌率
        return vo;
    }

    /**
     * 传入当前时间 上一次时间
     */
    private static BigDecimal computeRate(Long current, Long last) {
        BigDecimal result;
        if (last == null) {
            last = 0L;
        }
        if (last == 0L) {
            // 当上一期计数为零时，此时环比增长为倍数增长
            result = new BigDecimal((current - last) * 100);
        } else {
            result = BigDecimal.valueOf((current - last) * 100).divide(BigDecimal.valueOf(last), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        return result;
    }
}
