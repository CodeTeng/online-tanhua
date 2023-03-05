package com.tanhua.server.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLocation;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MessagesService;
import com.tanhua.server.service.TanhuaService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @DubboReference
    private QuestionApi questionApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;
    @DubboReference
    private UserLikeApi userLikeApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MessagesService messagesService;
    @DubboReference
    private UserLocationApi userLocationApi;

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

    @Override
    public TodayBest personalInfo(Long userId) {
        // 1. 查询用户详细信息
        UserInfo userInfo = userInfoApi.findById(userId);
        // 2. 根据操作人id和查看的用户id，查询两者的推荐数据
        RecommendUser recommendUser = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        // 3. 封装数据返回
        return TodayBest.init(userInfo, recommendUser);
    }

    @Override
    public String strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        if (question == null) {
            return "对方没有设置问题，请直接留言";
        }
        return question.getTxt();
    }

    @Override
    public void replyQuestions(Long userId, String reply) {
        // 构造消息数据
        Long currentUserId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(currentUserId);
        Map map = new HashMap();
        map.put("userId", currentUserId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + currentUserId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);
        String message = JSON.toJSONString(map);
        // 发送消息
        Boolean flag = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    @Override
    public List<TodayBest> queryCardsList() {
        // 1. 查询推荐列表，排除自己的喜欢和不喜欢
        List<RecommendUser> recommendUserList = recommendUserApi.queryCardsList(UserHolder.getUserId(), 10);
        // 2. 判断是否存在，不存在模拟数据
        if (recommendUserList.isEmpty()) {
            recommendUserList = new ArrayList<>();
            String[] userIds = recommendUser.split(",");
            for (String userId : userIds) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(userId));
                recommendUser.setToUserId(UserHolder.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                recommendUserList.add(recommendUser);
            }
        }
        // 3. 查询推荐的用户数据
        List<Long> ids = recommendUserList.stream().map(RecommendUser::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> infoMap = userInfoApi.findByIds(ids, null);
        // 4. 封装 vo 返回
        List<TodayBest> list = new ArrayList<>();
        recommendUserList.forEach(recommendUser -> {
            UserInfo userInfo = infoMap.get(recommendUser.getUserId());
            if (userInfo != null) {
                list.add(TodayBest.init(userInfo, recommendUser));
            }
        });
        return list;
    }

    @Override
    public void likeUser(Long likeUserId) {
        // 1. 调用 Dubbo 服务保存到 MongoDB 中
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, true);
        if (!save) {
            // 失败
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 保存喜欢数据到 redis 中，删除不喜欢数据
        stringRedisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        // 3. 判断是否双向喜欢，是双向喜欢，自动添加为好友
        if (isLikeOrNotLike(likeUserId, UserHolder.getUserId(), true)) {
            // 4. 添加好友
            messagesService.contacts(likeUserId);
        }
    }

    private boolean isLikeOrNotLike(Long likeUserId, Long userId, boolean isLike) {
        if (isLike) {
            String key = Constants.USER_LIKE_KEY + likeUserId;
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, userId.toString()));
        } else {
            String key = Constants.USER_NOT_LIKE_KEY + likeUserId;
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, userId.toString()));
        }
    }

    @Override
    public void notLikeUser(Long likeUserId) {
        // 1. 调用 Dubbo 服务保存到 MongoDB 中
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, false);
        if (!save) {
            // 失败
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 保存不喜欢数据到 redis 中，删除喜欢数据
        stringRedisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        // 3. 判断是否是双向不喜欢，如果是双向不喜欢，自动删除好友关系
        if (isLikeOrNotLike(likeUserId, UserHolder.getUserId(), false)) {
            // 4. 删除好友
            messagesService.delete(likeUserId);
        }
    }

    @Override
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        // 1. 查询附近距离的所有用户
        List<UserLocation> userLocationList = userLocationApi.queryNearUser(UserHolder.getUserId(), Double.valueOf(distance));
        if (userLocationList.isEmpty()) {
            // 随机模拟用户
            userLocationList = new ArrayList<>();
            String[] userIds = recommendUser.split(",");
            for (String userId : userIds) {
                UserLocation userLocation = userLocationApi.findByUserId(Long.valueOf(userId));
                userLocationList.add(userLocation);
            }
        }
        // 2. 根据附近用户id查询用户详细信息
        List<Long> userIdList = userLocationList.stream().map(UserLocation::getUserId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, userInfo);
        // 3. 封装数据返回
        List<NearUserVo> list = new ArrayList<>();
        for (UserLocation userLocation : userLocationList) {
            // 排除当前用户
            if (Objects.equals(userLocation.getUserId(), UserHolder.getUserId())) {
                continue;
            }
            UserInfo info = map.get(userLocation.getUserId());
            if (info != null) {
                list.add(NearUserVo.init(info));
            }
        }
        return list;
    }
}
