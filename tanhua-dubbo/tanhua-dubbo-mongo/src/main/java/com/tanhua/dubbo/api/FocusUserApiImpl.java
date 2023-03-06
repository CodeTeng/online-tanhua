package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 22:41
 */
@DubboService
public class FocusUserApiImpl implements FocusUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean hasFocus(Long uid, Long userId) {
        return mongoTemplate.exists(Query.query(Criteria.where("userId").is(userId).and("followUserId").is(uid)), FocusUser.class);
    }

    @Override
    public void save(FocusUser focusUser) {
        mongoTemplate.save(focusUser);
    }

    @Override
    public void delete(FocusUser focusUser) {
        Query query = Query.query(Criteria.where("userId").is(focusUser.getUserId()).and("followUserId").is(focusUser.getFollowUserId()));
        mongoTemplate.remove(query, FocusUser.class);
    }
}
