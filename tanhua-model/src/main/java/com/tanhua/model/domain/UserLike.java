package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 23:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_like")
public class UserLike implements Serializable {
    private static final long serialVersionUID = 6739966698394686523L;

    private ObjectId id;
    @Indexed
    private Long userId; //用户id，自己
    @Indexed
    private Long likeUserId; //喜欢的用户id，对方
    private Boolean isLike; // 是否喜欢
    private Long created; //创建时间
    private Long updated; // 更新时间
}
