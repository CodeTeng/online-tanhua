package com.tanhua.server.service.impl;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:19
 */
@Service
public class MessagesServiceImpl implements MessagesService {
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @Override
    public UserInfoVo findUserInfoByHuanxinId(String huanxinId) {
        // 1. 根据环信id查询用户信息
        User user = userApi.findByHuanxinId(huanxinId);
        // 2. 进而查询用户详细信息
        UserInfo userInfo = userInfoApi.findById(user.getId());
        // 3. 数据返回
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo); // copy同名同类型的属性
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }
}
