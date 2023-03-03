package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description: 好友时间线表，用于存储好友发布的数据
 * @author: ~Teng~
 * @date: 2023/3/2 19:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movement_timeLine")
public class MovementTimeLine implements Serializable {
    private static final long serialVersionUID = 9096178416317502524L;
    @Id
    private ObjectId id;
    private ObjectId movementId;// 动态id
    private Long userId;   // 发布动态用户id
    private Long friendId; // 可见好友id
    private Long created; //发布的时间
}
