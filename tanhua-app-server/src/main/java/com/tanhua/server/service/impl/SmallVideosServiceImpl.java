package com.tanhua.server.service.impl;

import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SmallVideosService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) {
        try {
            // 1. 上传封面图至OSS
            String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
            // 2. 上传视频至FastDFS
            String filename = videoFile.getOriginalFilename();
            String suffixFilename = filename.substring(filename.lastIndexOf(".") + 1);
            StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffixFilename, null);
            String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
            // 3. 保存Video到MongoDB中
            Video video = new Video();
            video.setUserId(UserHolder.getUserId());
            video.setPicUrl(imageUrl);
            video.setVideoUrl(videoUrl);
            video.setText("我就是我，不一样的烟火");
            videoApi.save(video);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorResult.error());
        }
    }

    @Override
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        // 1. 查询 redis 数据
        String redisKey = Constants.VIDEOS_RECOMMEND + UserHolder.getUserId();
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. 判断 redis 数据是否存在，并且判断 redis 数据是否满足本次分页条数
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        if (StringUtils.isNotBlank(redisValue)) {
            // 3. redis 数据存在，根据VID查询数据
            String[] values = redisValue.split(",");
            // 判断当前页的起始条数是否小于数组总数
            if ((page - 1) * pagesize < values.length) {
                List<Long> vids = Arrays.stream(values).skip((long) (page - 1) * pagesize)
                        .limit(pagesize).map(Long::valueOf).collect(Collectors.toList());
                // 根据vids查询推荐视频
                list = videoApi.findVideoByVids(vids);
            }
            // 计算已经多少页了
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }
        // 4. redis 数据不存在，分页查询视频数据
        if (list.isEmpty()) {
            // 需要考虑 page 大小
            list = videoApi.queryVideoList(page - redisPages, pagesize);
        }
        // 5. 查询用户信息
        List<Long> userIdList = list.stream().map(Video::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdList, null);
        // 6、构建返回值
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
}
