package com.tanhua.server.interceptor;

import com.tanhua.model.domain.User;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 10:08
 */
public class UserHolder {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    /**
     * 保存数据到线程
     */
    public static void set(User user) {
        threadLocal.set(user);
    }

    /**
     * 获取线程中的用户信息
     */
    public static User get() {
        return threadLocal.get();
    }


    /**
     * 从当前线程，获取用户对象的id
     */
    public static Long getUserId() {
        if (threadLocal.get() == null) {
            return null;
        }
        return threadLocal.get().getId();
    }

    /**
     * 从当前线程，获取用户对象的手机号码
     */
    public static String getMobile() {
        if (threadLocal.get() == null) {
            return null;
        }
        return threadLocal.get().getMobile();
    }

    /**
     * 移除线程中数据
     */
    public static void remove() {
        threadLocal.remove();
    }
}
