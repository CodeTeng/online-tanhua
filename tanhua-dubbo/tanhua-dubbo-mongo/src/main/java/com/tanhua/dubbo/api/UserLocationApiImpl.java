package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:28
 */
@DubboService
public class UserLocationApiImpl implements UserLocationApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            // 1. 根据用户id查询用户信息
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            if (userLocation == null) {
                // 不存在 保存
                userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
                userLocation.setAddress(address);
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                mongoTemplate.insert(userLocation);
            } else {
                // 存在 更新地理坐标
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("address", address)
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", userLocation.getLastUpdated());
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<UserLocation> queryNearUser(Long userId, Double metre) {
        // 1. 根据用户id查询用户信息
        UserLocation userLocation = mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserLocation.class);
        if (userLocation == null) {
            return null;
        }
        // 2. 已当前用户位置绘制原点
        GeoJsonPoint geoJsonPoint = userLocation.getLocation();
        // 3. 绘制半径
        Distance distance = new Distance(metre / 1000, Metrics.KILOMETERS);
        // 4. 绘制圆形
        Circle circle = new Circle(geoJsonPoint, distance);
        // 5. 查询
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        return mongoTemplate.find(query, UserLocation.class);
    }

    @Override
    public UserLocation findByUserId(Long userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserLocation.class);
    }
}
