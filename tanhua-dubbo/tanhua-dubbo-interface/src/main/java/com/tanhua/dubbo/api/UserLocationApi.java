package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.UserLocation;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:28
 */
public interface UserLocationApi {
    /**
     * 更新地理位置
     */
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    /**
     * 查询附件所有用户信息id
     */
    List<Long> queryNearUser(Long userId, Double metre, String recommendUser);

    /**
     * 根据用户id查询用户位置
     */
    UserLocation findByUserId(Long userId);
}
