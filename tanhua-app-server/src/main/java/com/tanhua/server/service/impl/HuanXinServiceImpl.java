package com.tanhua.server.service.impl;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.HuanXinService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:16
 */
@Service
public class HuanXinServiceImpl implements HuanXinService {
    @DubboReference
    private UserApi userApi;

    @Override
    public HuanXinUserVo findHuanXinUser() {
        Long userId = UserHolder.getUserId();
        User user = userApi.findById(userId);
        if (user == null) {
            return null;
        }
        return new HuanXinUserVo(user.getHxUser(), user.getHxPassword());
    }
}
