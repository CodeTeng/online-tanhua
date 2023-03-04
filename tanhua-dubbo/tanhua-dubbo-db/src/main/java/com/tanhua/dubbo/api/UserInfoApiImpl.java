package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:19
 */
@DubboService
public class UserInfoApiImpl implements UserInfoApi {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", userIds);
        if (userInfo != null) {
            queryWrapper.lt(userInfo.getAge() != null, "age", userInfo.getAge());
            queryWrapper.eq(StringUtils.isNoneBlank(userInfo.getGender()), "gender", userInfo.getGender());
            queryWrapper.like(StringUtils.isNoneBlank(userInfo.getNickname()), "nickname", userInfo.getNickname());
        }
        List<UserInfo> userInfoList = userInfoMapper.selectList(queryWrapper);
        return CollUtil.fieldValueMap(userInfoList, "id");
    }
}
