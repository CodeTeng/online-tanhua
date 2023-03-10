package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description: 访客表
 * @author: ~Teng~
 * @date: 2023/3/6 19:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visitors")
public class Visitors implements Serializable {
    private static final long serialVersionUID = 2811682148052386573L;

    private ObjectId id;
    private Long userId; // 我的id
    private Long visitorUserId; // 来访用户id
    private String from; // 来源，如首页、圈子等
    private Long date; // 来访时间
    private String visitDate;// 来访日期
    private Double score; // 得分
}
