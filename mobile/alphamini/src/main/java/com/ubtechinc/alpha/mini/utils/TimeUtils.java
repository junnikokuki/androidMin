package com.ubtechinc.alpha.mini.utils;

import android.text.TextUtils;

import com.ubtechinc.alpha.mini.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ubt on 2018/2/2.
 */

public class TimeUtils {

    public static final String[] weekDays = ResourceUtils.getStringArray(R.array.weekdays);

    public static final long DATE_DURATION = 24 * 60 * 60 * 1000;

    /**
     * 获取星期几
     *
     * @param date
     * @return
     */
    public static String getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    /**
     * 获取当前日期的显示字符串，当天显示为“今天”，一天前显示为“昨天”，
     * 两天以上，本周之内“星期N”，两天以上，本周之外为“年/月/日”
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        Calendar today = Calendar.getInstance();
        long todayTime = today.getTimeInMillis();
        long currentTime = date.getTime();
        float deltaTime = (todayTime - currentTime) / 86400000;
        if (deltaTime < 1) {
            return ResourceUtils.getString(R.string.today);
        } else if (deltaTime < 2) {
            return ResourceUtils.getString(R.string.yesterday);
        } else if (deltaTime < 7) {
            return getWeekDay(date);
        } else {
            return getDateString(date, null);
        }
    }

    public static String getDateString(Date date, String formatPattern) {
        if (TextUtils.isEmpty(formatPattern)) {
            formatPattern = ResourceUtils.getString(R.string.day_format);
        }
        DateFormat dateFormat = new SimpleDateFormat(formatPattern);
        return dateFormat.format(date);
    }


    /**
     * 获取当前时间的显示字符串“时：分：秒”
     *
     * @param date
     * @return
     */
    public static String getTimeString(Date date) {
        return getTimeString(date, null);
    }

    public static String getTimeString(Date date, String formatPattern) {
        if (TextUtils.isEmpty(formatPattern)) {
            formatPattern = ResourceUtils.getString(R.string.time_format);
        }
        DateFormat timeFormat = new SimpleDateFormat(formatPattern);
        return timeFormat.format(date);
    }

    /**
     * 时长＜60s时，显示xx秒钟
     * 60s≤时长≤60min时，显示xx分钟
     * 时长＞60min时，显示xx小时xx分钟
     *
     * @return
     */
    public static String getDurationString(long duration) {
        if (duration < 60) {
            int second = (int) (duration);
            return ResourceUtils.getString(R.string.duration_second, second);
        } else if (duration < 3600) {
            int minute = (int) (duration / 60);
            return ResourceUtils.getString(R.string.duration_minute, minute);
        } else {
            int hour = (int) (duration / 3600);
            int minute = (int) (duration % 3600);
            return ResourceUtils.getString(R.string.duration_hour, hour, minute);
        }
    }
}
