package com.tf.transfer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huangyue
 * @date 2018/11/09 16:49
 * @Description
 */
public class DateUtil {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getNowTimeFormat() {
        return format.format(new Date());
    }

    public static String getTimeString(String formatString) {
        try {
            return formatAll.format(format.parse(formatString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

}
