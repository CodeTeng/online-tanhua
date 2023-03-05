package com.tanhua.server.service.impl;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.BaiduService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 15:27
 */
@Service
public class BaiduServiceImpl implements BaiduService {
    @DubboReference
    private UserLocationApi userLocationApi;

    @Override
    public void updateLocation(Double longitude, Double latitude, String address) {
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(), longitude, latitude, address);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
