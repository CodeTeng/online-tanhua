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
import com.tanhua.model.mongo.Visitors;
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

import java.text.SimpleDateFormat;
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
    @DubboReference
    private VisitorsApi visitorsApi;

    @Override
    public TodayBest todayBest() {
        // 1. ????????????id
        Long userId = UserHolder.getUserId();
        // 2. ??????????????????
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            // ??????????????????
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }
        // 3. ??? recommendUser ????????? TodayBest
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        return TodayBest.init(userInfo, recommendUser);
    }

    @Override
    public PageResult recommendation(RecommendUserDto dto) {
        // 1???????????????id
        Integer page = dto.getPage();
        Integer pagesize = dto.getPagesize();
        Long userId = UserHolder.getUserId();
        // 2?????????recommendUserApi????????????????????????
        PageResult pr = recommendUserApi.queryRecommendUserList(page, pagesize, userId);
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        if (items == null || items.size() <= 0) {
            return pr;
        }
        // 3. ???????????????????????????id
        List<Long> userIdList = items.stream().map(RecommendUser::getUserId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        // 4. ?????????????????????????????????
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
        // 1. ????????????????????????
        UserInfo userInfo = userInfoApi.findById(userId);
        // 2. ???????????????id??????????????????id??????????????????????????????
        RecommendUser recommendUser = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        // 3. ?????????????????? ??????
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(UserHolder.getUserId());
        visitors.setDate(System.currentTimeMillis());
        visitors.setFrom("??????");
        visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        visitors.setScore(recommendUser.getScore());
        visitorsApi.save(visitors);
        // 4. ??????????????????
        return TodayBest.init(userInfo, recommendUser);
    }

    @Override
    public String strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        if (question == null) {
            return "??????????????????????????????????????????";
        }
        return question.getTxt();
    }

    @Override
    public void replyQuestions(Long userId, String reply) {
        // ??????????????????
        Long currentUserId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(currentUserId);
        Map map = new HashMap();
        map.put("userId", currentUserId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + currentUserId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);
        String message = JSON.toJSONString(map);
        // ????????????
        Boolean flag = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    @Override
    public List<TodayBest> queryCardsList() {
        // 1. ??????????????????????????????????????????????????????
        List<RecommendUser> recommendUserList = recommendUserApi.queryCardsList(UserHolder.getUserId(), 10);
        // 2. ??????????????????????????????????????????
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
        // 3. ???????????????????????????
        List<Long> ids = recommendUserList.stream().map(RecommendUser::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> infoMap = userInfoApi.findByIds(ids, null);
        // 4. ?????? vo ??????
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
        // 1. ?????? Dubbo ??????????????? MongoDB ???
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, true);
        if (!save) {
            // ??????
            throw new BusinessException(ErrorResult.error());
        }
        // 2. ????????????????????? redis ???????????????????????????
        stringRedisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        // 3. ??????????????????????????????????????????????????????????????????
        if (isLikeOrNotLike(likeUserId, UserHolder.getUserId(), true)) {
            // 4. ????????????
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
        // 1. ?????? Dubbo ??????????????? MongoDB ???
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, false);
        if (!save) {
            // ??????
            throw new BusinessException(ErrorResult.error());
        }
        // 2. ???????????????????????? redis ????????????????????????
        stringRedisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        // 3. ????????????????????????????????????????????????????????????????????????????????????
        if (isLikeOrNotLike(likeUserId, UserHolder.getUserId(), false)) {
            // 4. ????????????
            messagesService.delete(likeUserId);
        }
    }

    @Override
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        // 1. ?????????????????????????????????
        List<Long> userIdList = userLocationApi.queryNearUser(UserHolder.getUserId(), Double.valueOf(distance), recommendUser);
        // 2. ??????????????????id????????????????????????
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, userInfo);
        // 3. ??????????????????
        List<NearUserVo> list = new ArrayList<>();
        for (Long userId : userIdList) {
            // ??????????????????
            if (Objects.equals(userId, UserHolder.getUserId())) {
                continue;
            }
            UserInfo info = map.get(userId);
            if (info != null) {
                list.add(NearUserVo.init(info));
            }
        }
        return list;
    }
}
