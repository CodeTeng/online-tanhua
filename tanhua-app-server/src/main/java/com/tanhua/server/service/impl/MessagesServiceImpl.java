package com.tanhua.server.service.impl;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.ContactVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MessagesService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 16:19
 */
@Service
public class MessagesServiceImpl implements MessagesService {
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @DubboReference
    private FriendApi friendApi;

    @Override
    public UserInfoVo findUserInfoByHuanxinId(String huanxinId) {
        // 1. 根据环信id查询用户信息
        User user = userApi.findByHuanxinId(huanxinId);
        // 2. 进而查询用户详细信息
        UserInfo userInfo = userInfoApi.findById(user.getId());
        // 3. 数据返回
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo); // copy同名同类型的属性
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    @Override
    public void contacts(Long friendId) {
        // 1. 注册到环信中
        Boolean flag = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + UserHolder.getUserId(), Constants.HX_USER_PREFIX + friendId);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 添加到 mongoDB 中
        friendApi.save(UserHolder.getUserId(), friendId);
    }

    @Override
    public PageResult findFriends(Integer page, Integer pagesize, String keyword) {
        // 1. 查询当前用户的所有好友数据
        List<Friend> list = friendApi.findByUserId(UserHolder.getUserId(), page, pagesize);
        if (list.isEmpty()) {
            // 没有好友
            return new PageResult();
        }
        // 2. 根据好友id集合查询好友详细信息
        List<Long> friendIdList = list.stream().map(Friend::getFriendId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(friendIdList, userInfo);
        // 3. 构造 VO 返回
        List<ContactVo> voList = new ArrayList<>();
        list.forEach(friend -> {
            UserInfo info = map.get(friend.getFriendId());
            if (info != null) {
                ContactVo contactVo = ContactVo.init(info);
                voList.add(contactVo);
            }
        });
        return new PageResult(page, pagesize, 0, voList);
    }
}
