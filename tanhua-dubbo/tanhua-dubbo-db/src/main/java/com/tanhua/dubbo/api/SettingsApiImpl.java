package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:15
 */
@DubboService
public class SettingsApiImpl implements SettingsApi {
    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long userId) {
        return settingsMapper.selectOne(new QueryWrapper<Settings>().eq("user_id", userId));
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
