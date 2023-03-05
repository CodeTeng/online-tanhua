package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 14:56
 */
@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        Query query = Query.query(Criteria.where("toUserId").is(toUserId))
                .with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long userId) {
        Query query = Query.query(Criteria.where("toUserId").is(userId))
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(pagesize).skip((long) (page - 1) * pagesize);
        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);
        long count = mongoTemplate.count(query, RecommendUser.class);
        return new PageResult(page, pagesize, (int) count, recommendUserList);
    }

    @Override
    public RecommendUser queryByUserId(Long recommendUserId, Long userId) {
        Query query = Query.query(Criteria.where("toUserId").is(userId).and("userId").is(recommendUserId));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(recommendUserId);
            recommendUser.setToUserId(userId);
            // 构建缘分值
            recommendUser.setScore(90d);
        }
        return recommendUser;
    }

    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        // 1. 查询用户自己喜欢和不喜欢的用户数据
        List<UserLike> userLikeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserIdList = userLikeList.stream().map(UserLike::getLikeUserId).collect(Collectors.toList());
        // 2. 排除自己喜欢和不喜欢的用户，查询推荐用户
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIdList)),
                Aggregation.sample(count));
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        return results.getMappedResults();
    }
}
