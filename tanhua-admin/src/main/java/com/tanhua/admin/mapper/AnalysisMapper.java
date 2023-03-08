package com.tanhua.admin.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.admin.Analysis;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 14:44
 */
@Repository
public interface AnalysisMapper extends BaseMapper<Analysis> {
    /**
     * 累计注册用户数
     */
    @Select("select sum(num_registered) from tb_analysis")
    Long totalUser();

    /**
     * 根据大于传入时间统计数量 过去7天活跃用户  过去30天活跃用户
     */
    @Select("select sum(num_active) from tb_analysis where record_date >  #{offsetDay}")
    long countActive(DateTime offsetDay);

    /**
     * 今日 昨天 新增用户数量
     */
    @Select("select num_registered from tb_analysis where record_date = #{date}")
    Long countNewUsers(String date);

    /**
     * 今日 昨天 登录次数
     */
    @Select("select num_login from tb_analysis where record_date = #{date}")
    Long countLoginTimes(String date);

    /**
     * 今日 昨天 活跃用户数量
     */
    @Select("select num_active from tb_analysis where record_date = #{date}")
    Long countActiveUsers(String date);
}
