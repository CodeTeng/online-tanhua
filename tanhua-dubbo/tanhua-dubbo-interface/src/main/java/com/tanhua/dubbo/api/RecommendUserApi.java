package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 14:54
 */
public interface RecommendUserApi {
    /**
     * 查询今日佳人数据
     *
     * @param toUserId 用户id
     * @return 推荐用户
     */
    RecommendUser queryWithMaxScore(Long toUserId);

    /**
     * 分页查询数据列表
     */
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long userId);

    /**
     * 根据推荐用户id和当前用户id查询推荐用户信息
     */
    RecommendUser queryByUserId(Long recommendUserId, Long userId);

    /**
     * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     */
    List<RecommendUser> queryCardsList(Long userId, int count);
}
