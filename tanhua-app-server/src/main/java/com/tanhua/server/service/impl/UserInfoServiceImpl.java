package com.tanhua.server.service.impl;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:17
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Override
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    @Override
    public void updateHead(MultipartFile headPhoto, Long id) {
        String filename = headPhoto.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = headPhoto.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 1. 通过 oss 上传头像
        String avatarUrl = ossTemplate.upload(filename, inputStream);
        // 2. 通过百度 api 判断是否包含人脸
        boolean flag = aipFaceTemplate.detect(avatarUrl);
        if (!flag) {
            throw new RuntimeException("不包含人脸");
        } else {
            // 包含人脸，更新用户详细信息
            UserInfo userInfo = new UserInfo();
            userInfo.setAvatar(avatarUrl);
            userInfo.setId(id);
            userInfoApi.update(userInfo);
        }
    }

    @Override
    public UserInfoVo findById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        if (userInfo.getAge() != null) {
            userInfoVo.setAge(userInfo.getAge().toString());
        }
        return userInfoVo;
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
