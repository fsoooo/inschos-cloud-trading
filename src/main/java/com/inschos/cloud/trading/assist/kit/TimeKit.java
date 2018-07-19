package com.inschos.cloud.trading.assist.kit;

import com.inschos.common.assist.kit.StringKit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/19
 * version  v1.0.0
 */
public class TimeKit {

    /**
     * 格式化时间戳用
     *
     * @param sdf      格式
     * @param showDate 时间
     * @return showDate指定sdf的格式
     */
    public static String parseMillisecondByShowDate(SimpleDateFormat sdf, String showDate) {
        if (!StringKit.isEmpty(showDate)) {
            try {
                Date parse = parse = sdf.parse(showDate);
                return String.valueOf(parse.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 格式化时间戳用
     *
     * @param sdfs 时间格式集合
     * @return showDate指定sdf的格式
     */
    public static String parseMillisecondByShowDate(List<SimpleDateFormat> sdfs, String showDate) {
        if (sdfs == null || sdfs.isEmpty() || StringKit.isEmpty(showDate)) {
            return null;
        }

        String result = null;
        for (SimpleDateFormat sdf : sdfs) {
            result = parseMillisecondByShowDate(sdf, showDate);
            if (!StringKit.isEmpty(result)) {
                break;
            }
        }
        return result;
    }

}
