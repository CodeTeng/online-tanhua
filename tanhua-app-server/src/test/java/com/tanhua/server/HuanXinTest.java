package com.tanhua.server;

import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 15:05
 */
@SpringBootTest
public class HuanXinTest {
    private EMService emService;

    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @DubboReference
    private UserApi userApi;

    @BeforeEach
    public void init() {
        EMProperties properties = EMProperties.builder()
                .setAppkey("1111230304175061#tanhua")
                .setClientId("YXA6APsROq6LTimLTOLi_f4I1Q")
                .setClientSecret("YXA6eTjZW2Ku7VBfF4xt2RATJqWNzq0")
                .build();
        emService = new EMService(properties);
    }

    @Test
    public void test() {
        // 创建环信用户
//        emService.user().create("user01", "123456").block();
//        emService.user().create("user02", "123456").block();
        // 添加好友
//        emService.contact().add("user01", "user02").block();
        // 删除好友
//        emService.contact().remove("user01", "user02").block();
        // 服务端发送消息
        Set<String> set = CollUtil.newHashSet("1234");
        emService.message().send("user01", "users", set, new EMTextMessage().text("Java"), null).block();
    }

    /**
     * 批量注册
     */
    @Test
    public void testRegister() {
        List<User> users = userApi.findAll();
        for (User user : users) {
            Boolean create = huanXinTemplate.createUser(Constants.HX_USER_PREFIX + user.getId(), Constants.INIT_PASSWORD);
            if (create) {
                user.setHxUser(Constants.HX_USER_PREFIX + user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }
    }
}
