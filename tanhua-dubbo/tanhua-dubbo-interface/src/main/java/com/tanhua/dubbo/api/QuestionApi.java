package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 11:18
 */
public interface QuestionApi {
    /**
     * 根据用户id查询该用户设置的问题
     *
     * @param userId 用户id
     * @return 设置问题
     */
    Question findByUserId(Long userId);

    /**
     * 添加陌生人问题
     *
     * @param question 问题
     */
    void save(Question question);

    /**
     * 更新陌生人问题
     *
     * @param question 问题
     */
    void update(Question question);
}
