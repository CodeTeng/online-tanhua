package com.tanhua.server;

import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 21:07
 */
@SpringBootTest
public class MovementApiTest {
    @DubboReference
    private MovementApi movementApi;

    @Test
    public void testPublish() {
        Movement movement = new Movement();
        movement.setUserId(107L);
        movement.setTextContent("你的酒窝没有酒，我却醉的像条狗");
        List<String> list = new ArrayList<>();
        list.add("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_1.png");
        list.add("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_2.png");
        movement.setMedias(list);
        movement.setLatitude("23.185674");
        movement.setLongitude("113.422544");
        movement.setLocationName("中国重庆市南岸区学府大道66号");
        movementApi.publish(movement);
    }
}
