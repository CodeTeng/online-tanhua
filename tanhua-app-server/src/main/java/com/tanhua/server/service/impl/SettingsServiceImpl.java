package com.tanhua.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:10
 */
@Service
public class SettingsServiceImpl implements SettingsService {
    @DubboReference
    private SettingsApi settingsApi;
    @DubboReference
    private QuestionApi questionApi;
    @DubboReference
    private BlackListApi blackListApi;

    @Override
    public SettingsVo settings() {
        SettingsVo settingsVo = new SettingsVo();
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        settingsVo.setId(userId);
        // 2. 获取用户手机号
        String mobile = UserHolder.getMobile();
        settingsVo.setPhone(mobile);
        // 3. 查询该用户的通用设置
        Settings settings = settingsApi.findByUserId(userId);
        if (settings != null) {
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
            settingsVo.setLikeNotification(settings.getLikeNotification());
        }
        // 4. 查用该用户设置的问题
        Question question = questionApi.findByUserId(userId);
        if (question != null) {
            settingsVo.setStrangerQuestion(question.getTxt());
        }
        return settingsVo;
    }

    @Override
    public void saveQuestion(String content) {
        Long userId = UserHolder.getUserId();
        Question question = questionApi.findByUserId(userId);
        if (question == null) {
            // 添加新问题
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.save(question);
        } else {
            // 更新问题
            question.setTxt(content);
            questionApi.update(question);
        }
    }

    @Override
    public void saveSettings(Map map) {
        Boolean likeNotification = (Boolean) map.get("likeNotification");
        Boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        Boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
        Long userId = UserHolder.getUserId();
        Settings settings = settingsApi.findByUserId(userId);
        if (settings == null) {
            // 首次添加设置
            settings = new Settings();
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setUserId(userId);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.save(settings);
        } else {
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.update(settings);
        }
    }

    @Override
    public PageResult blacklist(int page, int size) {
        Long userId = UserHolder.getUserId();
        // 根据用户id查询黑名单
        IPage<UserInfo> userInfoIPage = blackListApi.findByUserId(userId, page, size);
        // 封装返回
        return new PageResult(page, size, (int) userInfoIPage.getTotal(), userInfoIPage.getRecords());
    }

    @Override
    public void deleteBlackList(Long blackUserId) {
        Long userId = UserHolder.getUserId();
        blackListApi.deleteById(userId, blackUserId);
    }
}
