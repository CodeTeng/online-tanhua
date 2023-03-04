package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description: 圈子互动表（点赞，评论，喜欢）
 * @author: ~Teng~
 * @date: 2023/3/3 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comment")
public class Comment implements Serializable {
    @Id
    private ObjectId id;
    private ObjectId publishId;    //动态id
    private Integer commentType;   //评论类型，1-点赞，2-评论，3-喜欢
    private String content;        //评论内容
    private Long userId;           //评论人
    private Long publishUserId;    //被评论人ID
    private Long created;           //发表时间
    private Integer likeCount = 0; //当前评论的点赞数
}
