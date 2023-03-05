package com.tanhua.server;

import com.tanhua.dubbo.api.UserLocationApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:36
 */
@SpringBootTest
public class UserLocationApiTest {
    @DubboReference
    private UserLocationApi userLocationApi;

    @Test
    public void testUpdateUserLocation() {
        userLocationApi.updateLocation(1L, 106.550814, 29.609201, "重庆北站");
        userLocationApi.updateLocation(2L, 106.551107,29.611637, "重庆北站北广场");
        userLocationApi.updateLocation(3L, 106.550942,29.606452, "重庆北站南广场");
        userLocationApi.updateLocation(4L, 106.57716, 29.55729,"解放碑");
        userLocationApi.updateLocation(5L, 106.578757,29.562117, "洪崖洞");
        userLocationApi.updateLocation(6L, 106.580793,29.562767, "朝天门");
        userLocationApi.updateLocation(7L, 106.533919,29.576104, "观音桥");
        userLocationApi.updateLocation(8L, 106.604257,29.546887, "南山一棵树风景区");
        userLocationApi.updateLocation(9L, 116.459958, 39.937193, "德云社(三里屯店)");
        userLocationApi.updateLocation(10L, 116.333374, 40.009645, "清华大学");
        userLocationApi.updateLocation(41L, 116.316833, 39.998877, "北京大学");
        userLocationApi.updateLocation(42L, 117.180115, 39.116464, "天津大学(卫津路校区)");
        userLocationApi.updateLocation(107L, 106.571006, 29.488388, "重庆交通大学");
        userLocationApi.updateLocation(108L, 106.580243, 29.505781, "重庆工商大学");
    }
}
