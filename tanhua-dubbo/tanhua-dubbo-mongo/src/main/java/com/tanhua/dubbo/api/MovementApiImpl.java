package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
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
 * @date: 2023/3/2 19:02
 */
@DubboService
public class MovementApiImpl implements MovementApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TimeLineService timeLineService;

    @Override
    public void publish(Movement movement) {
        try {
            // 1. 保存文章信息
            movement.setPid(idWorker.getNextId("movement"));
            movement.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movement);
            // 2. 查询当前用户的好友数据
//            Query query = Query.query(Criteria.where("userId").is(movement.getUserId()));
//            List<Friend> friendList = mongoTemplate.find(query, Friend.class);
//            if (friendList.size() == 0) {
//                // 没有好友，直接返回
//                return;
//            }
//            // 3. 循环好友数据，构造好友时间线
//            friendList.forEach(friend -> {
//                MovementTimeLine movementTimeLine = new MovementTimeLine();
//                movementTimeLine.setMovementId(movement.getId());
//                movementTimeLine.setUserId(friend.getUserId());
//                movementTimeLine.setFriendId(friend.getFriendId());
//                movementTimeLine.setCreated(System.currentTimeMillis());
//                mongoTemplate.save(movementTimeLine);
//            });
            // 异步执行
            timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
        } catch (Exception e) {
            // todo 事务处理
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        Query query = Query.query(Criteria.where("userId").is(userId))
                .skip((long) (page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movementList = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, 0, movementList);
    }
}
