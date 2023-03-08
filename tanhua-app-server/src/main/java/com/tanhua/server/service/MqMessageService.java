package com.tanhua.server.service;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:28
 */
public interface MqMessageService {
    /**
     * 发送日志消息
     *
     * @param userId 用户id
     * @param type   操作类型,
     *               0101为登录，
     *               0102为注册，
     *               0201为发动态，
     *               0202为浏览动态，
     *               0203为动态点赞，
     *               0204为动态喜欢，
     *               0205为评论，
     *               0206为动态取消点赞，
     *               0207为动态取消喜欢，
     *               0301为发小视频，
     *               0302为小视频点赞，
     *               0303为小视频取消点赞，
     *               0304为小视频评论
     * @param key    用户相关user , 动态相关movement , 小视频相关 video
     * @param busId  业务id  动态id或者视频id
     */
    void sendLogMessage(Long userId, String type, String key, String busId);
}
