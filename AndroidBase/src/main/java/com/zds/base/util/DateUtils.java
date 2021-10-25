package com.zds.base.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 1022 on 2019/5/7.
 * 时间工具
 */
public class DateUtils {
    public static DateFormat format_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

    private static final String YMDHM_FORMAT = "yyyy-MM-dd HH:mm";
    public static DateFormat format_yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static DateFormat format_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat format_downline_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    public static String getFormatTimes() {
        return format_yyyy_MM_dd_HH_mm_ss.format(now());
    }

    public static String getDownLineFormatTimes() {
        return format_downline_yyyy_MM_dd_HH_mm_ss.format(now());
    }

    public static String getFormatDay() {
        return format_yyyy_MM_dd.format(now());
    }

    /**
     * 是否是今天或未来
     *
     * @param date 输入时间
     * @return
     */
    public static boolean isTomorrow(final Date date) {
        //今天的结束时间
        Date endDate = dayEnd(now());
        if (date.getTime() > endDate.getTime()) {
            return true;
        }
        return false;
    }

    public static boolean isTomorrow(String date) {
        return isTomorrow(formatData(date));
    }

    /**
     * 是否是明天可未来
     *
     * @param date
     * @return
     */
    public static boolean isToday(final Date date) {
        return isTheDay(date == null ? DateUtils.now() : date, DateUtils.now());
    }

    /**
     * 指定时间离现在是否在多少天前/后内
     *
     * @param theDay 指定时间
     * @param days   天数(如为负数则是多少天前内,正数是多少天后内)
     */
    public static boolean isInDate(final Date theDay, int days) {
        // TODO: 2021/1/22 用date减
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(theDay.getTime());

        //基准天
        Calendar nowToDay = Calendar.getInstance();
        nowToDay.add(Calendar.DATE, days);
        if (days > 0) {
            //基准天前
            return cd.before(nowToDay);
        } else if (days < 0) {
            //基准天后
            return cd.after(nowToDay);
        } else {
            return true;
        }
    }


    /**
     * 服务器北京时间转为本地时间
     */
    public static String stringChinaToLocal(String srcTime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String convertTime = "";

        try {// 将输入时间字串转换为UTC时间
            Date chinseDate = sdf.parse(srcTime);

            Calendar cd = Calendar.getInstance();
            cd.setTimeInMillis(chinseDate.getTime());

            SimpleDateFormat sdfGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            convertTime = sdfGmt.format(cd.getTime());

        } catch (Exception e) {
            return srcTime;
        }

        return convertTime;

    }


    /**
     * local ---> UTC
     *
     * @return
     */
    public static String date2UTC(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHM_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtTime = sdf.format(data);
        return gmtTime;
    }

    /**
     * UTC --->local
     *
     * @param utcTime UTC
     * @return
     */
    public static String utc2Local(String utcTime) {
        try {
            if (TextUtils.isEmpty(utcTime)) {
                return "";
            }
            SimpleDateFormat utcFormater = new SimpleDateFormat(YMDHM_FORMAT);
            utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gpsUTCDate = null;
            try {
                gpsUTCDate = utcFormater.parse(utcTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat localFormater = new SimpleDateFormat(YMDHM_FORMAT);
            localFormater.setTimeZone(TimeZone.getDefault());
            String localTime = localFormater.format(gpsUTCDate.getTime());
            return localTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isToday(String date) {
        return isToday(formatData(date));
    }

    public static Date now() {
        Date nowdate = new Date();
        return nowdate;
    }

    /**
     * 是否是指定日期
     *
     * @param date
     * @param day
     * @return
     */
    public static boolean isTheDay(final Date date, final Date day) {
        return date.getTime() >= DateUtils.dayBegin(day).getTime()
                && date.getTime() <= DateUtils.dayEnd(day).getTime();
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date == null ? new Date() : date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date == null ? new Date() : date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 获取时间区域 判定
     * -1 区域时间前
     * 0  区域时间内
     * 1  区域时间后
     */
    public static int checkInAreaTime(Date start, Date end) {
        Date now = now();
        if (now.getTime() < start.getTime()) {//小于开始时间
            return -1;
        }
        if (now.getTime() > end.getTime()) {//大于结束时间
            return 1;
        }
        return 0;
    }

    /**
     * 获取时间区域 判定
     * -1 区域时间前
     * 0  区域时间内
     * 1  区域时间后
     *
     * @param start yyyy-MM-dd HH:mm格式开始时间
     * @param start yyyy-MM-dd HH:mm格式结束时间
     */
    public static int checkInAreaTime(String start, String end) {
        return checkInAreaTime(formatData(start, format_yyyy_MM_dd_HH_mm), formatData(end, format_yyyy_MM_dd_HH_mm));
    }

    /**
     * 当前是否开始时间前min分钟 之内
     *
     * @param start yyyy-MM-dd HH:mm格式开始时间
     */
    public static int isBefore15Min(String start) {
        return isBeforeMin(start, 15);
    }

    /**
     * 当前是否开始时间前min分钟 之内
     *
     * @param start yyyy-MM-dd HH:mm格式开始时间
     * @param min   分钟时间 15：15分钟前
     * @return -1 区域时间前;0  区域时间内; 1  区域时间后
     */
    public static int isBeforeMin(String start, int min) {

        Date end = formatData(start, format_yyyy_MM_dd_HH_mm);

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(end);
        startTime.add(Calendar.MINUTE, -min);//15分钟前

        return checkInAreaTime(startTime.getTime(), end);
    }

    /**
     * 获取时间差异
     *
     * @param startTime 开始的时间
     * @param endTime   结束的时间
     * @return 格式化的时间
     */
    public static String getDiffTimeFormat(Date startTime, Date endTime) {
        long diff = startTime.getTime() - endTime.getTime();
        return formatTime(diff);
    }

    /**
     * 转化为时间
     */
    public static Date formatData(String dateString, DateFormat format) {
        if (!StringUtil.isEmpty(dateString) && format != null) {
            try {
                return format.parse(dateString);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 转化为时间
     */
    public static Date formatData(String dateString) {
        if (!StringUtil.isEmpty(dateString)) {
            try {
                return format_yyyy_MM_dd_HH_mm.parse(dateString);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 转化为时间
     */
    public static Date formatDataSS(String dateString) {
        if (!StringUtil.isEmpty(dateString)) {
            try {
                return format_yyyy_MM_dd_HH_mm_ss.parse(dateString);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    public static String formatTime(long time) {
        time = time / 1000;
        String strHour = "" + (time / 3600);
        String strMinute = "" + time % 3600 / 60;
        String strSecond = "" + time % 3600 % 60;
        strHour = strHour.length() < 2 ? "0" + strHour : strHour;
        strMinute = strMinute.length() < 2 ? "0" + strMinute : strMinute;
        strSecond = strSecond.length() < 2 ? "0" + strSecond : strSecond;
        String strRsult = "";

        if (!strHour.equals("00")) {
            strRsult += strHour + ":";
        }
        //if(!strMinute.equals("00")){
        strRsult += strMinute + ":";
        //}
        strRsult += strSecond;
        return strRsult;
    }


    /**
     * 时间戳转换为字符串
     */
    public static String getDateToString(long milSecond) {
        try {
            Date date = new Date(milSecond);
            return format_yyyy_MM_dd_HH_mm_ss.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前日期是一个月中的几号
     */
    public static int getCurrDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


}
