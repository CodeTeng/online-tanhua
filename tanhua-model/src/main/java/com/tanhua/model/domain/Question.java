package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 问题表实体
 * @author: ~Teng~
 * @date: 2023/3/2 11:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {
    private Long id;
    private Long userId;
    //问题内容
    private String txt;
    private Date created;
    private Date updated;
}
