package com.tanhua.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 16:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendUserDto implements Serializable {
    private Integer page = 1; //当前页数
    private Integer pagesize = 10; //页尺寸
    private String gender; //性别 man woman
    private String lastLogin; //近期登陆时间
    private Integer age; //年龄
    private String city; //居住地
    private String education; //学历
}
