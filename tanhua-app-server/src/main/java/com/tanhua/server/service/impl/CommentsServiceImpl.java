package com.tanhua.server.service.impl;

import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/3 14:35
 */
@Service
@Slf4j
public class CommentsServiceImpl implements CommentsService {
    @DubboReference
    private CommentApi commentApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        // 1. 查询评论列表
        List<Comment> commentList = commentApi.findComments(movementId, CommentType.COMMENT, page, pagesize);
        if (commentList.isEmpty()) {
            return new PageResult();
        }
        // 2. 查询所有的评论用户详细数据
        List<Long> commentUserIdList = commentList.stream().map(Comment::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(commentUserIdList, null);
        // 3. 封装数据返回
        List<CommentVo> vos = new ArrayList<>();
        commentList.forEach(comment -> {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo != null) {
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        });
        return new PageResult(page, pagesize, 0, vos);
    }

    @Override
    public void saveComments(String movementId, String content) {
        // 1、获取操作用户id
        Long userId = UserHolder.getUserId();
        // 2、构造Comment
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        // 3、保存评论 并获取评论数量
        Integer commentCount = commentApi.save(comment);
        log.info("评论数量：{}", commentCount);
    }

    @Override
    public Integer likeComment(String movementId) {
        // 1. 查询该用户是否已经点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (hasComment) {
            throw new BusinessException(ErrorResult.likeError());
        }
        // 2. 保存到 mongo 中
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.save(comment);
        // 3. 保存到 redis 中
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().put(key, hashKey, "1");
        return count;
    }

    @Override
    public Integer dislikeComment(String movementId) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (!hasComment) {
            // 未点赞 无法取消点赞
            throw new BusinessException(ErrorResult.disLikeError());
        }
        // 调用API，删除数据，返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        // 拼接redis的key，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().delete(key, hashKey);
        return count;
    }

    @Override
    public Integer loveComment(String movementId) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LOVE);
        if (hasComment) {
            throw new BusinessException(ErrorResult.loveError());
        }
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.save(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().put(key, hashKey, "1");
        return count;
    }

    @Override
    public Integer disloveComment(String movementId) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LOVE);
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disLoveError());
        }
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().delete(key, hashKey);
        return count;
    }
}
