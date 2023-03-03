package com.tanhua.server.service.impl;

import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.TanhuaService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 15:43
 */
@Service
public class TanhuaServiceImpl implements TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @Override
    public TodayBest todayBest() {
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 查询推荐用户
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            // 模拟推荐用户
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }
        // 3. 将 recommendUser 转换为 TodayBest
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        return TodayBest.init(userInfo, recommendUser);
    }

    @Override
    public PageResult recommendation(RecommendUserDto dto) {
        // 1、获取用户id
        Integer page = dto.getPage();
        Integer pagesize = dto.getPagesize();
        Long userId = UserHolder.getUserId();
        // 2、调用recommendUserApi分页查询数据列表
        PageResult pr = recommendUserApi.queryRecommendUserList(page, pagesize, userId);
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        if (items == null || items.size() <= 0) {
            return pr;
        }
        // 3. 提取所有推荐用户的id
        List<Long> userIdList = items.stream().map(RecommendUser::getUserId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        // 4. 批量查询所有的用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, userInfo);
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser recommendUser : items) {
            UserInfo info = map.get(recommendUser.getUserId());
            if (info != null) {
                TodayBest todayBest = TodayBest.init(info, recommendUser);
                list.add(todayBest);
            }
        }
        pr.setItems(list);
        return pr;
    }
}
