package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/3 14:37
 */
@DubboService
public class CommentApiImpl implements CommentApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize) {
        Query query = Query.query(Criteria.where("publishId").is(movementId).and("commentType").is(comment.getType()))
                .skip((long) (page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Comment.class);
    }

    @Override
    public Integer save(Comment comment) {
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        mongoTemplate.save(comment);
        // 更新动态表中的字段
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else {
            update.inc("loveCount", 1);
        }
        // 设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        // 获取更新后的最新数据
        options.returnNew(true);
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        return modify.statisticsCount(comment.getCommentType());
    }
}
