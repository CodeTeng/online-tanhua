package com.tanhua.server;

import cn.hutool.core.util.RandomUtil;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 21:54
 */
public class Test {
    public static void main(String[] args) {
        int i = RandomUtil.randomInt(100, 1000);
        System.out.println(i);
        String s = RandomUtil.randomNumbers(3);
        System.out.println(s);
    }
}
