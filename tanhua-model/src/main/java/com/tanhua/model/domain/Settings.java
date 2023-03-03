package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 设置表实体
 * @author: ~Teng~
 * @date: 2023/3/2 11:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Settings implements Serializable {
    private Long id;
    private Long userId;
    private Boolean likeNotification;
    private Boolean pinglunNotification;
    private Boolean gonggaoNotification;
    private Date created;
    private Date updated;
}
