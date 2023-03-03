package com.tanhua.dubbo;

import com.tanhua.commons.utils.DateUtils;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.mongo.RecommendUser;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 18:22
 */
@SpringBootTest
public class MongoTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 模拟添加推荐人数据
     */
    @Test
    public void insertRecommendUserTest() throws ParseException {
        List<RecommendUser> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (int i = 1; i <= 99; i++) {
            RecommendUser recommendUser = new RecommendUser();
            double score = new BigDecimal(RandomUtils.nextDouble(50, 100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            recommendUser.setToUserId(107L);
            recommendUser.setScore(score);
            recommendUser.setUserId((long) i);
            recommendUser.setDate(sdf.format(DateUtils.randomDate()));
            list.add(recommendUser);
        }
        mongoTemplate.insertAll(list);
    }

    /**
     * 模拟添加朋友数据
     */
    @Test
    public void insertFriendTest() throws ParseException {
        List<Friend> list = new ArrayList<>();
        for (int i = 1; i <= 99; i++) {
            Friend friend = new Friend();
            friend.setUserId((long) i);
            friend.setFriendId(107L);
            Date date = DateUtils.randomDate();
            friend.setCreated(date.getTime());
            list.add(friend);
        }
        mongoTemplate.insertAll(list);
    }

    /**
     * 模拟添加时间线数据
     */
    @Test
    public void insertMovementTimeLineTest() throws ParseException {
        List<MovementTimeLine> list = new ArrayList<>();
        List<Movement> movementList = mongoTemplate.find(Query.query(Criteria.where("userId").is(107L)), Movement.class);
        List<ObjectId> movementIdList = movementList.stream().map(Movement::getId).collect(Collectors.toList());
        for (ObjectId movementId : movementIdList) {
            for (int i = 1; i <= 99; i++) {
                MovementTimeLine movementTimeLine = new MovementTimeLine();
                movementTimeLine.setUserId(107L);
                movementTimeLine.setMovementId(movementId);
                movementTimeLine.setFriendId((long) i);
                Date date = DateUtils.randomDate();
                movementTimeLine.setCreated(date.getTime());
                list.add(movementTimeLine);
            }
//            MovementTimeLine movementTimeLine = new MovementTimeLine();
//            movementTimeLine.setUserId(1L);
//            movementTimeLine.setMovementId(movementId);
//            movementTimeLine.setFriendId(107L);
//            movementTimeLine.setCreated(DateUtils.randomDate().getTime());
//            list.add(movementTimeLine);
        }
        mongoTemplate.insertAll(list);
    }
}
