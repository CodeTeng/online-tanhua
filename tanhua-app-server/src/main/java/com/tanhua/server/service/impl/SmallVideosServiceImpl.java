package com.tanhua.server.service.impl;

import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.FocusUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MqMessageService;
import com.tanhua.server.service.SmallVideosService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:23
 */
@Service
public class SmallVideosServiceImpl implements SmallVideosService {
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;
    @DubboReference
    private VideoApi videoApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private FocusUserApi focusUserApi;
    @Autowired
    private MqMessageService mqMessageService;

    @Override
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) {
        try {
            // 1. ??????????????????OSS
            String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
            // 2. ???????????????FastDFS
            String filename = videoFile.getOriginalFilename();
            String suffixFilename = filename.substring(filename.lastIndexOf(".") + 1);
            StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffixFilename, null);
            String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
            // 3. ??????Video???MongoDB???
            Video video = new Video();
            video.setUserId(UserHolder.getUserId());
            video.setPicUrl(imageUrl);
            video.setVideoUrl(videoUrl);
            video.setText("?????????????????????????????????");
            videoApi.save(video);
            mqMessageService.sendLogMessage(UserHolder.getUserId(), "0301", "videos", video.getId().toHexString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorResult.error());
        }
    }

    @Override
    @Cacheable(
            value = "videos",
            key = "T(com.tanhua.server.interceptor.UserHolder).getUserId()+'_'+#page+'_'+#pagesize")
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        // 1. ?????? redis ??????
        String redisKey = Constants.VIDEOS_RECOMMEND + UserHolder.getUserId();
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. ?????? redis ????????????????????????????????? redis ????????????????????????????????????
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        if (StringUtils.isNotBlank(redisValue)) {
            // 3. redis ?????????????????????VID????????????
            String[] values = redisValue.split(",");
            // ??????????????????????????????????????????????????????
            if ((page - 1) * pagesize < values.length) {
                List<Long> vids = Arrays.stream(values).skip((long) (page - 1) * pagesize)
                        .limit(pagesize).map(Long::valueOf).collect(Collectors.toList());
                // ??????vids??????????????????
                list = videoApi.findVideoByVids(vids);
            }
            // ????????????????????????
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }
        // 4. redis ??????????????????????????????????????????
        if (list.isEmpty()) {
            // ???????????? page ??????
            list = videoApi.queryVideoList(page - redisPages, pagesize);
        }
        // 5. ??????????????????
        List<Long> userIdList = list.stream().map(Video::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, null);
        // 6??????????????????
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo info = map.get(video.getUserId());
            if (info != null) {
                VideoVo vo = VideoVo.init(info, video);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0, vos);
    }

    @Override
    public void useFocus(Long uid) {
        // 1. ??????????????????????????????
        Boolean hasFocus = focusUserApi.hasFocus(uid, UserHolder.getUserId());
        if (hasFocus) {
            // ??????????????????
            throw new BusinessException(ErrorResult.focusError());
        }
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(uid);
        focusUser.setCreated(System.currentTimeMillis());
        // 2. ?????? MongoDB
        focusUserApi.save(focusUser);
        // 3. ????????? redis ???
        String key = Constants.MOVEMENTS_INTERACT_KEY + uid;
        String hashKey = Constants.FOCUS_USER_KEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().put(key, hashKey, "1");
    }

    @Override
    public void useUnFocus(Long uid) {
        // 1. ??????????????????????????????
        Boolean hasFocus = focusUserApi.hasFocus(uid, UserHolder.getUserId());
        if (!hasFocus) {
            // ??????????????????
            throw new BusinessException(ErrorResult.focusError());
        }
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(uid);
        // 2. ??? MongoDB ?????????
        focusUserApi.delete(focusUser);
        // 3. ??? redis ?????????
        String key = Constants.MOVEMENTS_INTERACT_KEY + uid;
        String hashKey = Constants.FOCUS_USER_KEY + UserHolder.getUserId();
        stringRedisTemplate.opsForHash().delete(key, hashKey);
    }
}
