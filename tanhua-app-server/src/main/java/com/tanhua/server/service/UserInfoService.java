package com.tanhua.server.service;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:17
 */
public interface UserInfoService {
    /**
     * 保存用户详细信息
     *
     * @param userInfo 用户详细信息
     */
    void save(UserInfo userInfo);

    /**
     * 上传用户头像
     *
     * @param headPhoto 用用户头像
     * @param id        用户id
     */
    void updateHead(MultipartFile headPhoto, Long id);

    /**
     * 根据用户id查询用户详细信息
     *
     * @param userId 用户id
     * @return 用户详细信息VO
     */
    UserInfoVo findById(Long userId);

    /**
     * 更新用户详细信息
     *
     * @param userInfo 用户详细信息
     */
    void update(UserInfo userInfo);
}
