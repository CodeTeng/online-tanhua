package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description: 关注用户表
 * @author: ~Teng~
 * @date: 2023/3/6 22:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "focus_user")
public class FocusUser implements Serializable {
    private ObjectId id;
    private Long userId;//用户id
    private Long followUserId;//关注用户id
    private Long created;
}
