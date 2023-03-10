package com.tanhua.server.service.impl;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.MQConstants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorsApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.MqMessageService;
import com.tanhua.server.service.UserFreezeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:03
 */
@Service
public class MovementsServiceImpl implements MovementsService {
    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private MovementApi movementApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private VisitorsApi visitorsApi;
    @Autowired
    private UserFreezeService userFreezeService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        // ?????????????????????????????????????????????
        Long userId = UserHolder.getUserId();
        userFreezeService.checkUserStatus("3", userId);
        // 1. ??????????????????????????????
        String content = movement.getTextContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorResult.contentError());
        }
        // 2. ??????????????????id
        List<String> medias = new ArrayList<>();
        // 3. ??????????????? OSS
        for (MultipartFile multipartFile : imageContent) {
            String filename = multipartFile.getOriginalFilename();
            InputStream inputStream = multipartFile.getInputStream();
            String url = ossTemplate.upload(filename, inputStream);
            medias.add(url);
        }
        // 3. ??????????????????Movement??????
        movement.setMedias(medias);
        movement.setUserId(userId);
        // 4. ??????API??????????????????
        movementApi.publish(movement);
        // ??????????????? rabbitMQ
        mqMessageService.sendLogMessage(userId, "0201", "movement", movement.getId().toHexString());
        // ????????????????????????
        amqpTemplate.convertAndSend(MQConstants.AUDIT_EXCHANGE, MQConstants.AUDIT_ROUTING_KEY, movement.getId().toHexString());
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        // 1. ????????????id??????????????????
        PageResult pageResult = movementApi.findByUserId(userId, page, pagesize, null);
        List<Movement> items = (List<Movement>) pageResult.getItems();
        if (items.size() == 0) {
            return pageResult;
        }
        // 2. ????????????????????????
        UserInfo userInfo = userInfoApi.findById(userId);
        // 3. ?????? vo
        List<MovementsVo> list = new ArrayList<>();
        items.forEach(movement -> {
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            list.add(movementsVo);
        });
        // ??????????????? rabbitMQ
        mqMessageService.sendLogMessage(userId, "0202", "movement", null);
        // 4. ????????????
        pageResult.setItems(list);
        return pageResult;
    }

    @Override
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        // 1????????????????????????id
        Long userId = UserHolder.getUserId();
        // 2?????????API?????????????????????????????????????????????
        List<Movement> list = movementApi.findFriendMovements(page, pagesize, userId);
        mqMessageService.sendLogMessage(userId, "0202", "movement", null);
        // 3???????????????????????????
        return getPageResult(page, pagesize, list);
    }

    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        if (list.isEmpty()) {
            return new PageResult();
        }
        // 4???????????????????????????id??????
        List<Long> userIds = list.stream().map(Movement::getUserId).collect(Collectors.toList());
        // 5??????????????????id????????????????????????
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        // 6?????????Movement????????????vo??????
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
                String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
                if (stringRedisTemplate.opsForHash().hasKey(key, hashKey)) {
                    vo.setHasLiked(1);
                }
                key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
                hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
                if (stringRedisTemplate.opsForHash().hasKey(key, hashKey)) {
                    vo.setHasLoved(1);
                }
                vos.add(vo);
            }
        }
        // 7?????????PageResult?????????
        return new PageResult(page, pagesize, 0, vos);
    }

    @Override
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        // 1. ??? redis ?????????????????????
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. ???????????????????????????
        List<Movement> list = new ArrayList<>();
        if (StringUtils.isBlank(redisValue)) {
            // ????????? ??????10?????????
            list = movementApi.randomMovements(pagesize);
        } else {
            // ?????????????????????pid?????? "16,17,18,19,20,21,10015,10020,10040,10064,10092,10093,10099,10067" 15
            String[] values = redisValue.split(",");
            // ??????????????????????????????????????????????????????
            if ((page - 1) * pagesize < values.length) {
                List<Long> pidList = Arrays.stream(values)
                        .skip((long) (page - 1) * pagesize)
                        .limit(pagesize).map(Long::valueOf).collect(Collectors.toList());
                // 3. ?????? api ??????????????????
                list = movementApi.findMovementsByPids(pidList);
            }
        }
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0202", "movement", null);
        return getPageResult(page, pagesize, list);
    }

    @Override
    public MovementsVo findById(String movementId) {
        // 1. ????????????
        Movement movement = movementApi.findById(movementId);
        if (movement == null) {
            return null;
        }
        // 2. ????????????????????????????????????
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0202", "movement", movementId);
        return MovementsVo.init(userInfo, movement);
    }

    @Override
    public List<VisitorsVo> queryVisitorsList() {
        // 1. ??????????????????
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(UserHolder.getUserId());
        String value = (String) stringRedisTemplate.opsForHash().get(key, hashKey);
        Long date = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
        // 2. ??????????????????
        List<Visitors> list = visitorsApi.queryMyVisitors(date, UserHolder.getUserId());
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        // 3. ????????????????????????
        List<Long> visitedUserIdList = list.stream().map(Visitors::getVisitorUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(visitedUserIdList, null);
        // 4. ??????????????????
        List<VisitorsVo> visitorsVos = new ArrayList<>();
        list.forEach(visitors -> {
            UserInfo userInfo = map.get(visitors.getVisitorUserId());
            if (userInfo != null) {
                visitorsVos.add(VisitorsVo.init(userInfo, visitors));
            }
        });
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0202", "movement", null);
        return visitorsVos;
    }
}
