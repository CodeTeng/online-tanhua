package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: Dubbo 服务
 * @author: ~Teng~
 * @date: 2023/3/1 21:14
 */
@DubboService
public class UserApiImpl implements UserApi {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("mobile", mobile));
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
