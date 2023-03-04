package com.tanhua.model.vo;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 21:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementsVo implements Serializable {
    private String id; //动态id

    private Long userId; //用户id
    private String avatar; //头像
    private String nickname; //昵称
    private String gender; //性别 man woman
    private Integer age; //年龄
    private String[] tags; //标签

    private String textContent; //文字动态
    private String[] imageContent; //图片动态
    private String distance; //距离
    private String createDate; //发布时间 如: 10分钟前
    private Integer likeCount; //点赞数
    private Integer commentCount; //评论数
    private Integer loveCount; //喜欢数

    private Integer hasLiked; //是否点赞（1是，0否）
    private Integer hasLoved; //是否喜欢（1是，0否）

    public static MovementsVo init(UserInfo userInfo, Movement movement) {
        MovementsVo vo = new MovementsVo();
        // 设置动态数据
        BeanUtils.copyProperties(movement, vo);
        vo.setId(movement.getId().toHexString());
        // 设置用户数据
        BeanUtils.copyProperties(userInfo, vo);
        if (!StringUtils.isEmpty(userInfo.getTags())) {
            vo.setTags(userInfo.getTags().split(","));
        }
        // 图片列表
        vo.setImageContent(movement.getMedias().toArray(new String[]{}));
        // 距离
        int distance = RandomUtil.randomInt(100, 1000);
        vo.setDistance(distance + "米");
        Date date = new Date(movement.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        // 设置是否点赞(后续处理)
        vo.setHasLoved(0);
        vo.setHasLiked(0);
        return vo;
    }
}
