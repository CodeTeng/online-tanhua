package com.tanhua.server.service;

import com.tanhua.model.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:23
 */
public interface SmallVideosService {
    /**
     * 发布视频
     *
     * @param videoThumbnail 封面图
     * @param videoFile      视频文件
     */
    void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile);

    /**
     * 分页查询视频列表
     */
    PageResult queryVideoList(Integer page, Integer pagesize);

    /**
     * 用户关注视频作者
     */
    void useFocus(Long uid);

    /**
     * 用户取消关注视频作者
     */
    void useUnFocus(Long uid);
}
