package com.nju.software.xmltodb.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description 获取北京时区时间
 * @Author wxy
 * @Date 2024/4/23
 **/
public class TimeUtil {

    public static String getTime() {
        ZonedDateTime beijingTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return beijingTime.format(formatter);
    }

}
