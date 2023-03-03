package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 通用设置返回VO
 * @author: ~Teng~
 * @date: 2023/3/2 11:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsVo implements Serializable {
    private Long id;
    private String strangerQuestion = "";
    private String phone;
    private Boolean likeNotification = true;
    private Boolean pinglunNotification = true;
    private Boolean gonggaoNotification = true;
}
