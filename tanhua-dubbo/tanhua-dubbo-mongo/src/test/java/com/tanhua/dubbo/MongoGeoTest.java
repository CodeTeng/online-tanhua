package com.tanhua.dubbo;

import com.tanhua.model.mongo.Places;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:02
 */
@SpringBootTest
public class MongoGeoTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testAdd() {
        Places places = new Places();
        places.setId(ObjectId.get());
        places.setAddress("湖北省武汉市东西湖区金山大道");
        places.setTitle("金山大道");
        places.setLocation(new GeoJsonPoint(114.226867, 30.636001));
        mongoTemplate.save(places);

        Places places2 = new Places();
        places2.setId(ObjectId.get());
        places2.setAddress("湖北省武汉市东西湖区奥园东路");
        places2.setTitle("奥园东路");
        places2.setLocation(new GeoJsonPoint(114.240592, 30.650171));
        mongoTemplate.save(places2);

        Places places3 = new Places();
        places3.setId(ObjectId.get());
        places3.setAddress("湖北省武汉市黄陂区X003");
        places3.setTitle("黄陂区X003");
        places3.setLocation(new GeoJsonPoint(114.355876, 30.726886));
        mongoTemplate.save(places3);

        Places places4 = new Places();
        places4.setId(ObjectId.get());
        places4.setAddress("湖北省武汉市黄陂区汉口北大道");
        places4.setTitle("汉口北大道");
        places4.setLocation(new GeoJsonPoint(114.364111, 30.722166));
        mongoTemplate.save(places4);
    }

    /**
     * 查询当前坐标附近的目标
     */
    @Test
    public void testNear() {
        // 构造坐标点
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(116.404, 39.915);
        // 构造半径
        Distance distance = new Distance(1, Metrics.KILOMETERS);
        // 画圆圈
        Circle circle = new Circle(geoJsonPoint, distance);
        // 构造 query 对象
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        List<Places> placesList = mongoTemplate.find(query, Places.class);
        placesList.forEach(System.out::println);
    }

    /**
     * 查询附近且获取间距
     */
    @Test
    public void testNear1() {
        // 1. 构造中心点(圆点)
        GeoJsonPoint point = new GeoJsonPoint(116.404, 39.915);
        // 2. 构建NearQuery对象
        NearQuery query = NearQuery.near(point, Metrics.KILOMETERS).maxDistance(1, Metrics.KILOMETERS);
        // 3. 调用mongoTemplate的geoNear方法查询
        GeoResults<Places> results = mongoTemplate.geoNear(query, Places.class);
        // 4. 解析GeoResult对象，获取距离和数据
        for (GeoResult<Places> result : results) {
            Places places = result.getContent();
            double value = result.getDistance().getValue();
            System.out.println(places+"---距离："+value + "km");
        }
    }
}
