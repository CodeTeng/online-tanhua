package com.tanhua.autoconfig.template;

import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import com.tanhua.autoconfig.properties.HuanXinProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 15:21
 */
@Slf4j
public class HuanXinTemplate {
    private EMService emService;

    public HuanXinTemplate(HuanXinProperties properties) {
        EMProperties emProperties = EMProperties.builder()
                .setAppkey(properties.getAppkey())
                .setClientId(properties.getClientId())
                .setClientSecret(properties.getClientSecret())
                .build();
        emService = new EMService(emProperties);
    }

    public Boolean createUser(String username, String password) {
        try {
            //创建环信用户
            emService.user().create(username.toLowerCase(), password).block();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建环信用户失败~");
        }
        return false;
    }

    //添加联系人
    public Boolean addContact(String username1, String username2) {
        try {
            //创建环信用户
            emService.contact().add(username1, username2).block();
            return true;
        } catch (Exception e) {
            log.error("添加联系人失败~");
        }
        return false;
    }

    // 删除联系人
    public Boolean deleteContact(String username1, String username2) {
        try {
            // 创建环信用户
            emService.contact().remove(username1, username2).block();
            return true;
        } catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }

    //发送消息
    public Boolean sendMsg(String username, String content) {
        try {
            // 接收人用户列表
            Set<String> set = CollUtil.newHashSet(username);
            // 文本消息
            EMTextMessage message = new EMTextMessage().text(content);
            // 发送消息  from：admin是管理员发送
            emService.message().send("admin", "users", set, message, null).block();
            return true;
        } catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }
}