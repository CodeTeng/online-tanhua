package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize, Integer state) {
        Query query = new Query();
        if (userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (state != null) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        // 查询总数
        int count = (int) mongoTemplate.count(query, Movement.class);
        // 设置分页参数
        query.skip((long) (page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> list = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, count, list);
    }

    @Override
    public List<Movement> findFriendMovements(Integer page, Integer pagesize, Long friendId) {
        // 1. 查询好友
        Query query = Query.query(Criteria.where("friendId").is(friendId))
                .skip((long) (page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<MovementTimeLine> movementTimeLineList = mongoTemplate.find(query, MovementTimeLine.class);
        if (movementTimeLineList.isEmpty()) {
            // 没有好友 直接返回
            return new ArrayList<>();
        }
        // 2. 提取好友线中的动态id
        List<ObjectId> objectIdList = movementTimeLineList.stream().map(MovementTimeLine::getMovementId).collect(Collectors.toList());
        // 3. 根据动态id查询动态集合
        return mongoTemplate.find(Query.query(Criteria.where("id").in(objectIdList)), Movement.class);
    }

    @Override
    public List<Movement> randomMovements(Integer counts) {
        // 1、创建统计对象，设置统计参数
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(counts));
        // 2、调用mongoTemplate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        // 3、获取统计结果
        return results.getMappedResults();
    }

    @Override
    public List<Movement> findMovementsByPids(List<Long> pidList) {
        return mongoTemplate.find(Query.query(Criteria.where("pid").in(pidList)), Movement.class);
    }

    @Override
    public Movement findById(String movementId) {
        return mongoTemplate.findById(movementId, Movement.class);
    }

    @Override
    public void update(String movementId, Integer state) {
        Query query = Query.query(Criteria.where("id").in(new ObjectId(movementId)));
        Update update = Update.update("state", state);
        mongoTemplate.updateFirst(query, update, Movement.class);
    }
}
