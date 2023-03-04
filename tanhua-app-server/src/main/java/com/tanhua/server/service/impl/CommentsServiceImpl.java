package com.tanhua.server.service.impl;

import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
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
}
