package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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
}
