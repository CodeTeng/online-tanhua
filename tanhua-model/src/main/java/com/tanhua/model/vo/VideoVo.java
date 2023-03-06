package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVo implements Serializable {
    private String id;
    private Long userId;
    private String avatar; //头像
    private String nickname; //昵称

    private String cover; //封面
    private String videoUrl; //视频URL
    private String signature; //发布视频时，传入的文字内容

    private Integer hasLiked; //是否已赞（1是，0否）
    private Integer hasFocus; //是否关注 （1是，0否）
    private Integer likeCount; //点赞数量
    private Integer commentCount; //评论数量

    public static VideoVo init(UserInfo userInfo, Video video) {
        VideoVo vo = new VideoVo();
        //copy用户属性
        BeanUtils.copyProperties(userInfo, vo);
        //copy视频属性
        BeanUtils.copyProperties(video, vo);
        vo.setCover(video.getPicUrl());
        vo.setId(video.getId().toHexString());
        vo.setSignature(video.getText());
        vo.setHasFocus(0);
        vo.setHasLiked(0);
        return vo;
    }
}
