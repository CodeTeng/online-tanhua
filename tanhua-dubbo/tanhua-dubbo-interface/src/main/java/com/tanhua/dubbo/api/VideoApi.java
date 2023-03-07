package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:30
 */
public interface VideoApi {
    /**
     * 保存小视频
     */
    void save(Video video);

    /**
     * 根据vids查询推荐视频
     */
    List<Video> findVideoByVids(List<Long> vids);

    /**
     * 分页查询小视频数据
     */
    List<Video> queryVideoList(int page, Integer pagesize);

    /**
     * 分页查询指定用户发布的所有视频列表
     */
    PageResult findAllVideos(Integer page, Integer pagesize, Long uid);
}
