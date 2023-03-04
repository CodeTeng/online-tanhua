package com.tanhua.server.service;

import com.tanhua.model.vo.HuanXinUserVo;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:16
 */
public interface HuanXinService {
    /**
     * 查询当前用户的环信账号
     */
    HuanXinUserVo findHuanXinUser();
}
