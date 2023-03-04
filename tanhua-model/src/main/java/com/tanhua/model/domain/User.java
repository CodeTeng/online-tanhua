package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 用户实体
 * @author: ~Teng~
 * @date: 2023/3/1 21:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Long id;
    private String mobile;
    private String password;
    private Date created;
    private Date updated;
    // 环信用户信息
    private String hxUser;
    private String hxPassword;
}
