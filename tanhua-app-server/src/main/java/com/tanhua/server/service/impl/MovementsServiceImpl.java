package com.tanhua.server.service.impl;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MovementsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:03
 */
@Service
public class MovementsServiceImpl implements MovementsService {
    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private MovementApi movementApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @Override
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        // 1. 判断发布内容是否为空
        String content = movement.getTextContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorResult.contentError());
        }
        // 2. 获取当前用户id
        Long userId = UserHolder.getUserId();
        List<String> medias = new ArrayList<>();
        // 3. 上传图片至 OSS
        for (MultipartFile multipartFile : imageContent) {
            String filename = multipartFile.getOriginalFilename();
            InputStream inputStream = multipartFile.getInputStream();
            String url = ossTemplate.upload(filename, inputStream);
            medias.add(url);
        }
        // 3. 将数据封装到Movement对象
        movement.setMedias(medias);
        movement.setUserId(userId);
        // 4. 调用API完成发布动态
        movementApi.publish(movement);
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        // 1. 根据用户id查询动态信息
        PageResult pageResult = movementApi.findByUserId(userId, page, pagesize);
        List<Movement> items = (List<Movement>) pageResult.getItems();
        if (items.size() == 0) {
            return pageResult;
        }
        // 2. 获取用户详细信息
        UserInfo userInfo = userInfoApi.findById(userId);
        // 3. 封装 vo
        List<MovementsVo> list = new ArrayList<>();
        items.forEach(movement -> {
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            list.add(movementsVo);
        });
        // 4. 封装返回
        pageResult.setItems(list);
        return pageResult;
    }
}
