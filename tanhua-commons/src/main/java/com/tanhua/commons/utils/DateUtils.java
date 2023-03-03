package com.tanhua.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 18:30
 */
public class DateUtils {
    public static Date randomDate() throws ParseException {
        //获取当前时间
        Calendar calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis();

        //设置指定时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = sdf.parse("2023/02/01");
        long start = date.getTime();

        Random random = new Random();

        //产生long类型指定范围随机数
        long randomDate = start + (long) (random.nextFloat() * (end - start + 1));

        return new Date(randomDate);
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (int i = 0; i < 100; i++) {
            System.out.println(sdf.format(randomDate()));
        }
    }
}
