package com.tanhua.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.service.UserFreezeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 9:59
 */
@Service
public class UserFreezeServiceImpl implements UserFreezeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void checkUserStatus(String state, Long userId) {
        String key = Constants.USER_FREEZE + userId;
        String value = stringRedisTemplate.opsForValue().get(key);
        // 存在
        if (StringUtils.isNotBlank(value)) {
            Map map = JSON.parseObject(value, Map.class);
            String freezingRange = (String) map.get("freezingRange");
            if (state.equals(freezingRange)) {
                throw new BusinessException(ErrorResult.builder().errMessage("用户已被冻结").build());
            }
        }
    }
}
