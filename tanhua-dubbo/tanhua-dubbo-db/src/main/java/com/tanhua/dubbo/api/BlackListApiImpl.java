package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 13:25
 */
@DubboService
public class BlackListApiImpl implements BlackListApi {
    @Autowired
    private BlackListMapper blackListMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public IPage<UserInfo> findByUserId(Long userId, int page, int size) {
        IPage<UserInfo> resPage = new Page<>(page, size);
        // 1. 根据用户id查询出该用户的所有黑名单用户id
        QueryWrapper<BlackList> blackListQueryWrapper = new QueryWrapper<>();
        blackListQueryWrapper.select("black_user_id").eq("user_id", userId);
        List<Long> blackUserIdList = blackListMapper.selectList(blackListQueryWrapper).stream().map(BlackList::getBlackUserId).collect(Collectors.toList());
        if (blackUserIdList.size() == 0) {
            // 无黑名单人员
            resPage.setTotal(0L);
            resPage.setRecords(new ArrayList<>());
            return resPage;
        }
        // 2. 根据黑名单用户id集合查询所有的用户详细信息
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.in("id", blackUserIdList);
        List<UserInfo> userInfoList = userInfoMapper.selectList(userInfoQueryWrapper);
        resPage.setRecords(userInfoList);
        resPage.setTotal(userInfoList.size());
        return resPage;
    }

    @Override
    public void deleteById(Long userId, Long blackUserId) {
        QueryWrapper<BlackList> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("black_user_id", blackUserId);
        blackListMapper.delete(qw);
    }
}
