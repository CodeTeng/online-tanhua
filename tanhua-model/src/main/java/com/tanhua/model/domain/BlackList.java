package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 黑名单表实体
 * @author: ~Teng~
 * @date: 2023/3/2 11:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackList implements Serializable {
    private Long id;
    private Long userId;
    private Long blackUserId;
    private Date created;
    private Date updated;
}
