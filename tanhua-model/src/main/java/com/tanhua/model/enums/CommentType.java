package com.tanhua.model.enums;

/**
 * @description: 评论类型：1-点赞，2-评论，3-喜欢
 * @author: ~Teng~
 * @date: 2023/3/3 14:27
 */
public enum CommentType {
    LIKE(1), COMMENT(2), LOVE(3);

    int type;

    CommentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
