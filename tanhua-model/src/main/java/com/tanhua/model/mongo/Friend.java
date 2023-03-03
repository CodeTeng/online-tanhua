package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description: 好友表:好友关系表
 * @author: ~Teng~
 * @date: 2023/3/2 19:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friend")
public class Friend implements Serializable {
    private static final long serialVersionUID = 6003135946820874230L;
    @Id
    private ObjectId id;
    private Long userId; //用户id
    private Long friendId; //好友id
    private Long created; //时间
}
