package com.tanhua.dubbo.utils;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 21:23
 */
@Component
public class TimeLineService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Async
    public void saveTimeLine(Long userId, ObjectId movementId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        if (friendList.size() == 0) {
            // 没有好友，直接返回
            return;
        }
        // 3. 循环好友数据，构造好友时间线
        friendList.forEach(friend -> {
            MovementTimeLine movementTimeLine = new MovementTimeLine();
            movementTimeLine.setMovementId(movementId);
            movementTimeLine.setUserId(friend.getUserId());
            movementTimeLine.setFriendId(friend.getFriendId());
            movementTimeLine.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movementTimeLine);
        });
    }
}
