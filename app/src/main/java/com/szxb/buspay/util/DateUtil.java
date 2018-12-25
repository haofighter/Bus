package com.szxb.buspay.util;

import android.util.Log;

import com.lilei.tool.tool.IToolInterface;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DateUtil {


    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("zh", "CN"));
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("zh", "CN"));
    private static SimpleDateFormat main_format = new SimpleDateFormat("yyyy.MM.dd\b\bHH:mm:ss", new Locale("zh", "CN"));
    private static SimpleDateFormat format_3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh", "CN"));

    /**
     * 时钟
     *
     * @return .
     */
    public static String getMainTime() {
        return String.format("%1$s", main_format.format(new Date()));
    }


    //得到当前日期：yyyy-MM-dd HH:mm:ss
    public static String getCurrentDate() {
        return format.format(new Date());
    }

    //自定义格式获取当前日期·
    public static String getCurrentDate(String format) {
        SimpleDateFormat ft = null;
        try {
            ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        } catch (Exception e) {
            return format_3.format(new Date());
        }
        return ft.format(new Date());
    }


    public static String getLastDate(String format) {
        SimpleDateFormat ft = null;
        try {
            ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        } catch (Exception e) {
            return format_3.format(new Date());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return ft.format(calendar.getTime());
    }

    //获取当时Long型日期
    public static long currentLong() {
        return System.currentTimeMillis() / 1000;
    }


    //当前时间的前n分钟
    public static String getCurrentDateLastMi(int time) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -time);// 1分钟之前的时间
        Date beforeD = beforeTime.getTime();
        return format.format(beforeD);
    }

    //当前时间的前n分钟
    public static String getCurrentDateLastMi(int time, String format) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -time);// 1分钟之前的时间
        Date beforeD = beforeTime.getTime();
        return new SimpleDateFormat(format, new Locale("zh", "CN")).format(beforeD);
    }


    public static long getMILLISECOND(String startTime, String endTime) {
        try {
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);
            long l = startDate.getTime() / 1000 - endDate.getTime() / 1000;
            Log.d("DateUtil",
                    "filterOpenID(DateUtil.java:211)相差时间=" + l);
            return l;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param type 0：公交卡，1：微信、银联卡
     * @return .
     */
    public static String[] time(int type) {
        if (type == 0) {
            String startTime = DateUtil.getTime("yyyyMMddHHmmss", 0, 0, 0, 0);
            String endTime = DateUtil.getTime("yyyyMMddHHmmss", 23, 59, 59, 999);
            return new String[]{startTime, endTime};
        } else {
            String startTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 0, 0, 0, 0);
            String endTime = DateUtil.getTime("yyyy-MM-dd HH:mm:ss", 23, 59, 59, 999);
            return new String[]{startTime, endTime};
        }
    }

    /**
     * @param format            格式：公交卡：yyyyMMddHHmmss,其他yyyy-MM-dd HH:mm:ss
     * @param hour_of_day_value 小时
     * @param minute_value      分钟
     * @param second_value      秒
     * @param millisecond_value 毫秒
     * @return 当前开始时间|结束时间
     */
    public static String getTime(String format, int hour_of_day_value,
                                 int minute_value, int second_value, int millisecond_value) {
        SimpleDateFormat ft = new SimpleDateFormat(format, new Locale("zh", "CN"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour_of_day_value);
        calendar.set(Calendar.MINUTE, minute_value);
        calendar.set(Calendar.SECOND, second_value);
        calendar.set(Calendar.MILLISECOND, millisecond_value);
        return ft.format(calendar.getTime());
    }

    /**
     * 设置k21时间
     */
    public static void setK21Time() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        if (year >= 2018) {
            try {
                SLog.d("DateUtil(setK21Time.java:155)开始校准K21时间>>>" + Calendar.getInstance().get(Calendar.YEAR));
                libszxb.deviceSettime(year, month, day, hour, min, sec);
            } catch (Exception e) {
                e.printStackTrace();
                SLog.d("DateUtil(setK21Time.java:112)校准K21时间异常>>" + e.toString());
            }
        }
    }

    /**
     * 校准时间
     *
     * @param time .
     */
    public static void setTime(String time, boolean isTip) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("zh", "CN"));
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.get(Calendar.YEAR);
            calendar.get(Calendar.MONTH);
            calendar.get(Calendar.DATE);
            calendar.get(Calendar.HOUR);
            calendar.get(Calendar.MINUTE);
            calendar.get(Calendar.SECOND);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            IToolInterface iToolInterface = BusApp.getInstance().getmService();
            if (iToolInterface != null) {
                iToolInterface.setDateTime(year, month, day, hour, min, second);
                setK21Time();
                if (isTip) {
                    BusToast.showToast(BusApp.getInstance(), "校准成功", true);
                }
            } else {
                if (isTip) {
                    BusToast.showToast(BusApp.getInstance(), "校准失败[NULL]", true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("DateUtil(setTime.java:190)时间校准失败>>" + e.toString());
            BusToast.showToast(BusApp.getInstance(), "校准失败\n" + e.toString(), false);
        }
    }


    //扫码当前时间的前day天
    public static String getScanCurrentDateLastDay(String format, int day) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.DATE, -day);//
        Date beforeD = beforeTime.getTime();
        SimpleDateFormat fd = new SimpleDateFormat(format, new Locale("zh", "CN"));
        return fd.format(beforeD);
    }

    /**
     * 文件是否过期
     *
     * @param lastDate 存储的时间
     * @return 是否删除
     */
    public static boolean isDelFile(Date lastDate) {
        String currentDate = getCurrentDate("yyyy-MM-dd");
        boolean lowTime = !currentDate.equals("1970-01-01");
        boolean heiTime = !currentDate.equals("2018-01-01");
        return differentDays(lastDate, Calendar.getInstance().getTime()) > 7 && lowTime && heiTime;
    }


    /**
     * 计算两个日期之间相差的天数
     *
     * @param lastDate    上次存储的日期
     * @param currentDate 现在的日期
     * @return 相差的天数
     */
    private static int differentDays(Date lastDate, Date currentDate) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(lastDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(currentDate);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    public static Date String2Date(String str) {
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

