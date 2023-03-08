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
        // 校验用户状态，判断是否已被冻结
        Long userId = UserHolder.getUserId();
        userFreezeService.checkUserStatus("3", userId);
        // 1. 判断发布内容是否为空
        String content = movement.getTextContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorResult.contentError());
        }
        // 2. 获取当前用户id
        List<String> medias = new ArrayList<>();
        // 3. 上传图片至 OSS
        for (MultipartFile multipartFile : imageContent) {
            String filename = multipartFile.getOriginalFilename();
            InputStream inputStream = multipartFile.getInputStream();
            String url = ossTemplate.upload(filename, inputStream);
            medias.add(url);
        }
        // 3. 将数据封装到Movement对象
        movement.setMedias(medias);
        movement.setUserId(userId);
        // 4. 调用API完成发布动态
        movementApi.publish(movement);
        // 发送日志到 rabbitMQ
        mqMessageService.sendLogMessage(userId, "0201", "movement", movement.getId().toHexString());
        // 发送动态审核消息
        amqpTemplate.convertAndSend(MQConstants.AUDIT_EXCHANGE, MQConstants.AUDIT_ROUTING_KEY, movement.getId().toHexString());
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        // 1. 根据用户id查询动态信息
        PageResult pageResult = movementApi.findByUserId(userId, page, pagesize, null);
        List<Movement> items = (List<Movement>) pageResult.getItems();
        if (items.size() == 0) {
            return pageResult;
        }
        // 2. 获取用户详细信息
        UserInfo userInfo = userInfoApi.findById(userId);
        // 3. 封装 vo
        List<MovementsVo> list = new ArrayList<>();
        items.forEach(movement -> {
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            list.add(movementsVo);
        });
        // 发送日志到 rabbitMQ
        mqMessageService.sendLogMessage(userId, "0202", "movement", null);
        // 4. 封装返回
        pageResult.setItems(list);
        return pageResult;
    }

    @Override
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        // 1、获取当前用户的id
        Long userId = UserHolder.getUserId();
        // 2、调用API查询当前用户好友发布的动态列表
        List<Movement> list = movementApi.findFriendMovements(page, pagesize, userId);
        mqMessageService.sendLogMessage(userId, "0202", "movement", null);
        // 3、判断列表是否为空
        return getPageResult(page, pagesize, list);
    }

    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        if (list.isEmpty()) {
            return new PageResult();
        }
        // 4、提取动态发布人的id列表
        List<Long> userIds = list.stream().map(Movement::getUserId).collect(Collectors.toList());
        // 5、根据用户的id列表获取用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        // 6、一个Movement构造一个vo对象
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
        // 7、构造PageResult并返回
        return new PageResult(page, pagesize, 0, vos);
    }

    @Override
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        // 1. 从 redis 中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. 判断是否有推荐数据
        List<Movement> list = new ArrayList<>();
        if (StringUtils.isBlank(redisValue)) {
            // 不存在 模拟10条数据
            list = movementApi.randomMovements(pagesize);
        } else {
            // 如果存在，处理pid数据 "16,17,18,19,20,21,10015,10020,10040,10064,10092,10093,10099,10067" 15
            String[] values = redisValue.split(",");
            // 判断当前页的起始条数是否小于数组总数
            if ((page - 1) * pagesize < values.length) {
                List<Long> pidList = Arrays.stream(values)
                        .skip((long) (page - 1) * pagesize)
                        .limit(pagesize).map(Long::valueOf).collect(Collectors.toList());
                // 3. 调用 api 查询推荐数据
                list = movementApi.findMovementsByPids(pidList);
            }
        }
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0202", "movement", null);
        return getPageResult(page, pagesize, list);
    }

    @Override
    public MovementsVo findById(String movementId) {
        // 1. 查询动态
        Movement movement = movementApi.findById(movementId);
        if (movement == null) {
            return null;
        }
        // 2. 根据动态查询用户详细信息
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0202", "movement", movementId);
        return MovementsVo.init(userInfo, movement);
    }

    @Override
    public List<VisitorsVo> queryVisitorsList() {
        // 1. 查询访问时间
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(UserHolder.getUserId());
        String value = (String) stringRedisTemplate.opsForHash().get(key, hashKey);
        Long date = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
        // 2. 查询访客列表
        List<Visitors> list = visitorsApi.queryMyVisitors(date, UserHolder.getUserId());
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        // 3. 查询用户详细信息
        List<Long> visitedUserIdList = list.stream().map(Visitors::getVisitorUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(visitedUserIdList, null);
        // 4. 构造数据返回
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
