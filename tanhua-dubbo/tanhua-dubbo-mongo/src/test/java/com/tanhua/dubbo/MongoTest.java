package com.tanhua.dubbo;

import com.tanhua.commons.utils.DateUtils;
import com.tanhua.model.mongo.RecommendUser;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
     * 模拟添加数据
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
}
