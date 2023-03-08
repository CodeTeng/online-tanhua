package com.tanhua.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.admin.service.ManagerService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/7 20:30
 */
@Service
public class ManagerServiceImpl implements ManagerService {
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private VideoApi videoApi;
    @DubboReference
    private MovementApi movementApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> iPage = userInfoApi.findAll(page, pagesize);
        // 将Ipage转化为PageResult
        return new PageResult(page, pagesize, (int) iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public UserInfo findUserById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        // 查询该用户的冻结状态
        String key = Constants.USER_FREEZE + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }

    @Override
    public PageResult findAllVideos(Integer page, Integer pagesize, Long uid) {
        return videoApi.findAllVideos(page, pagesize, uid);
    }

    @Override
    public PageResult findAllMovements(Integer page, Integer pagesize, Long userId, Integer state) {
        // 1. 查询 movement 对象
        PageResult result = movementApi.findByUserId(userId, page, pagesize, state);
        List<Movement> items = (List<Movement>) result.getItems();
        if (items.isEmpty()) {
            return new PageResult();
        }
        // 2. 查询用户详细信息
        List<Long> userIdList = items.stream().map(Movement::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, null);
        List<MovementsVo> vos = new ArrayList<>();
        items.forEach(movement -> {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        });
        // 3. 构造返回值
        result.setItems(vos);
        return result;
    }

    @Override
    public Map userFreeze(Map params) {
        // 构造 key 存入 redis 中
        String userId = params.get("userId").toString();
        Integer freezingTime = Integer.valueOf(params.get("freezingTime").toString());
        String key = Constants.USER_FREEZE + userId;
        // 失效时间
        int days = 0;
        if (freezingTime == 1) {
            days = 3;
        } else if (freezingTime == 2) {
            days = 7;
        }
        String value = JSON.toJSONString(params);
        // 存入 redis 中
        if (days > 0) {
            stringRedisTemplate.opsForValue().set(key, value, days, TimeUnit.DAYS);
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
        Map retMap = new HashMap();
        retMap.put("message", "冻结成功");
        return retMap;
    }

    @Override
    public Map userUnfreeze(Map params) {
        String userId = params.get("userId").toString();
        String key = Constants.USER_FREEZE + userId;
        // 删除redis数据
        stringRedisTemplate.delete(key);
        Map retMap = new HashMap();
        retMap.put("message", "解冻成功");
        return retMap;
    }
}
