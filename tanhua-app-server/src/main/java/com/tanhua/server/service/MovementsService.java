package com.tanhua.server.service;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 19:03
 */
public interface MovementsService {
    /**
     * 发布文章
     */
    void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException;

    /**
     * 查询我的动态
     */
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);

    /**
     * 查询好友动态
     */
    PageResult findFriendMovements(Integer page, Integer pagesize);

    /**
     * 查询推荐动态列表
     */
    PageResult findRecommendMovements(Integer page, Integer pagesize);

    /**
     * 查询单条动态
     */
    MovementsVo findById(String movementId);

    /**
     * 谁看过我
     */
    List<VisitorsVo> queryVisitorsList();
}
