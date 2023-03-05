package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
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
 * @date: 2023/3/4 17:34
 */
@DubboService
public class FriendApiImpl implements FriendApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        // 1. 保存自己的好友数据
        Query query1 = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        boolean flag1 = mongoTemplate.exists(query1, Friend.class);
        if (!flag1) {
            // 不存在 添加朋友
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
        // 2. 添加好友的数据
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        boolean flag2 = mongoTemplate.exists(query2, Friend.class);
        if (!flag2) {
            // 不存在 添加对方与我的关系
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }

    @Override
    public List<Friend> findByUserId(Long userId, Integer page, Integer pagesize) {
        Query query = Query.query(Criteria.where("userId").is(userId))
                .skip((long) (page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Friend.class);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        // 1. 删除自己的好友数据
        Query query1 = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        boolean flag1 = mongoTemplate.exists(query1, Friend.class);
        if (flag1) {
            mongoTemplate.remove(query1, Friend.class);
        }
        // 2. 删除好友的数据
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        boolean flag2 = mongoTemplate.exists(query2, Friend.class);
        if (flag2) {
            mongoTemplate.remove(query2, Friend.class);
        }
    }
}
