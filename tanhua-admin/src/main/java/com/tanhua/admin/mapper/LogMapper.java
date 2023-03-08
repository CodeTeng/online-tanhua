package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.admin.Log;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:47
 */
@Repository
public interface LogMapper extends BaseMapper<Log> {
    /**
     * 根据操作时间和类型统计日志统计用户数量
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE TYPE=#{type} AND log_time=#{logTime}")
    Integer queryByTypeAndLogTime(@Param("type") String type, @Param("logTime") String logTime);

    /**
     * 根据时间统计用户数量
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{logTime}")
    Integer queryByLogTime(String logTime);

    /**
     * 查询次日留存 , 从昨天活跃的用户中查询今日活跃用户
     */
    @Select("SELECT COUNT(DISTINCT user_id)  FROM tb_log WHERE log_time=#{today} AND user_id IN (SELECT user_id FROM tb_log WHERE TYPE='0102' AND log_time=#{yestoday})")
    Integer queryNumRetention1d(@Param("today") String today, @Param("yestoday") String yestoday);
}
