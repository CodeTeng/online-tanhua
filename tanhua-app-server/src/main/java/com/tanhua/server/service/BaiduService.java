package com.tanhua.server.service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:27
 */
public interface BaiduService {
    /**
     * 更新位置
     */
    void updateLocation(Double longitude, Double latitude, String address);
}
