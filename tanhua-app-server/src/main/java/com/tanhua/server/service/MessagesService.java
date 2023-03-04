package com.tanhua.server.service;

import com.tanhua.model.vo.UserInfoVo;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:19
 */
public interface MessagesService {
    /**
     * 根据环信ID查询用户详细信息
     */
    UserInfoVo findUserInfoByHuanxinId(String huanxinId);
}
