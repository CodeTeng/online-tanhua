package com.tanhua.commons.utils;

/**
 * @description: 常量类
 * @author: ~Teng~
 * @date: 2023/3/3 13:37
 */
public interface Constants {
    //手机APP短信验证码CHECK_CODE_
    String SMS_CODE = "CHECK_CODE_";
    // 图片验证码前缀
    String CAP_CODE = "CHECK_CODE_";

    //冻结用户状态KEY
    String USER_FREEZE = "USER_FREEZE_";

    //冻结HashKEY
    String FREEZE_USER = "FREEZE_USER_";

    //推荐动态
    String MOVEMENTS_RECOMMEND = "MOVEMENTS_RECOMMEND_";

    //推荐视频
    String VIDEOS_RECOMMEND = "VIDEOS_RECOMMEND_";

    //圈子互动KEY
    String MOVEMENTS_INTERACT_KEY = "MOVEMENTS_INTERACT_";

    //动态点赞用户HashKey
    String MOVEMENT_LIKE_HASHKEY = "MOVEMENT_LIKE_";

    //动态喜欢用户HashKey
    String MOVEMENT_LOVE_HASHKEY = "MOVEMENT_LOVE_";

    //视频点赞用户HashKey
    String VIDEO_LIKE_HASHKEY = "VIDEO_LIKE";

    //访问用户
    String VISITORS_USER = "VISITOR_USER";

    //初始化密码
    String INIT_PASSWORD = "123456";

    //环信用户前缀
    String HX_USER_PREFIX = "hx";

    //jwt加密盐
    String JWT_SECRET = "muziteng";

    //jwt超时时间
    int JWT_TIME_OUT = 3_600;

    //用户喜欢Redis的key
    String USER_LIKE_KEY = "USER_LIKE_SET_";

    //用户不喜欢Redis的key
    String USER_NOT_LIKE_KEY = "USER_NOT_LIKE_SET_";

    //关注用户的key
    String FOCUS_USER_KEY = "FOCUS_USER_KEY_";
}
