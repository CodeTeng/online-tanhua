package com.tanhua.server.service;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 15:43
 */
public interface TanhuaService {
    /**
     * 查询今日佳人
     */
    TodayBest todayBest();

    /**
     * 查询分页推荐好友列表
     */
    PageResult recommendation(RecommendUserDto dto);

    /**
     * 查询佳人信息
     */
    TodayBest personalInfo(Long userId);

    /**
     * 查看陌生人问题
     */
    String strangerQuestions(Long userId);

    /**
     * 回复陌生人问题
     */
    void replyQuestions(Long userId, String reply);

    /**
     * 探花-推荐用户列表
     */
    List<TodayBest> queryCardsList();

    /**
     * 喜欢
     */
    void likeUser(Long likeUserId);

    /**
     * 不喜欢
     */
    void notLikeUser(Long likeUserId);
}
