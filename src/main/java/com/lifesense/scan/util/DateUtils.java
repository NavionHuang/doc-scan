package com.lifesense.scan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 赵春定 on 2017/2/27.
 * 日期工具
 */
public class DateUtils {

    public static String[] getBetweenHour(Date date) {
        String formatMatch = "yyyy-MM-dd HH:mm:00";
        String endHour = getDateString(date, formatMatch);
        Date beforeOneHour = getBeforeTime(date, 1, 0, 0);
        String  startHour= getDateString(beforeOneHour, formatMatch);
        return new String[]{startHour, endHour};
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date        日期
     * @param formatMatch 格式，如yyyy-mm-dd
     * @return 格式化日期
     */
    public static String getDateString(Date date, String formatMatch) {
        SimpleDateFormat format = new SimpleDateFormat(formatMatch);
        return format.format(date);
    }

    public static Date getDate(String dateStr, String formatMatch) {
        SimpleDateFormat format = new SimpleDateFormat(formatMatch);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回修改后的时间
     *
     * @param date   日期
     * @param houre  小时
     * @param minute 分
     * @param second 秒
     * @return
     */
    public static Date getAfterTime(Date date, int houre, int minute, int second) {
        Calendar c = Calendar.getInstance();
        Date afterDate = null;
        if (date != null) {
            c.setTime(date);
            int currentHoure = c.get(Calendar.HOUR);
            int currentMinute = c.get(Calendar.MINUTE);
            int currentSecond = c.get(Calendar.SECOND);
            c.set(Calendar.HOUR, currentHoure + houre);
            c.set(Calendar.MINUTE, currentMinute + minute);
            c.set(Calendar.SECOND, currentSecond + second);
            afterDate = c.getTime();
        }
        return afterDate;
    }

    /**
     * 返回修改后的时间
     *
     * @param date   日期
     * @param houre  小时
     * @param minute 分
     * @param second 秒
     * @return
     */
    public static Date getBeforeTime(Date date, int houre, int minute, int second) {
        Calendar c = Calendar.getInstance();
        Date beforeDate = null;
        if (date != null) {
            c.setTime(date);
            int currentHoure = c.get(Calendar.HOUR);
            int currentMinute = c.get(Calendar.MINUTE);
            int currentSecond = c.get(Calendar.SECOND);
            c.set(Calendar.HOUR, currentHoure - houre);
            c.set(Calendar.MINUTE, currentMinute - minute);
            c.set(Calendar.SECOND, currentSecond - second);
            beforeDate = c.getTime();
        }
        return beforeDate;
    }


    /**
     * 返日期的后year年month月day日
     *
     * @param date 日期
     * @param day  日
     * @return 返还日期
     */
    public static Date getAfterDay(Date date, int day) {
        return getAfterDate(date, 0, 0, day);
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date 日期
     * @param day  日
     * @return 返还日期
     */
    public static Date getBeforeDay(Date date, int day) {
        return getBeforeDate(date, 0, 0, day);
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date  日期
     * @param month 月
     * @return 返还日期
     */
    public static Date getAfterMonth(Date date, int month) {
        return getAfterDate(date, 0, month, 0);
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date  日期
     * @param month 月
     * @return 返还日期
     */
    public static Date getBeforeMonth(Date date, int month) {
        return getBeforeDate(date, 0, month, 0);
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date  日期
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 返还日期
     */
    public static Date getBeforeDate(Date date, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        Date beforeDate = null;
        if (date != null) {
            c.setTime(date);
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH);
            int currentDay = c.get(Calendar.DATE);
            c.set(Calendar.YEAR, currentYear - year);
            c.set(Calendar.MONTH, currentMonth - month);
            c.set(Calendar.DATE, currentDay - day);
            beforeDate = c.getTime();
        }
        return beforeDate;
    }

    /**
     * 返日期的后year年month月day日
     *
     * @param date  日期
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 返还日期
     */
    public static Date getAfterDate(Date date, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        Date afterDate = null;
        if (date != null) {
            c.setTime(date);
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH);
            int currentDay = c.get(Calendar.DATE);
            c.set(Calendar.YEAR, currentYear + year);
            c.set(Calendar.MONTH, currentMonth + month);
            c.set(Calendar.DATE, currentDay + day);
            afterDate = c.getTime();
        }
        return afterDate;
    }

    /*返还[yyyy,MM,dd,HH,mm,ss]*/
    public static Integer[] getSplitDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Integer[] splitDate = new Integer[6];
        splitDate[0] = c.get(Calendar.YEAR);
        splitDate[1] = c.get(Calendar.MONTH);//由于Calendar.Calendar.MONTH 从0开始
        splitDate[2] = c.get(Calendar.DATE);
        splitDate[3] = c.get(Calendar.HOUR);
        splitDate[4] = c.get(Calendar.MINUTE);
        splitDate[5] = c.get(Calendar.SECOND);
        return splitDate;
    }

    /*
    * 比较两个日期
    *
    * */
    public static int betweenDay(Date current, Date compare) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(current);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(compare);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }


    public static void main(String[] args) {
        Date date = new Date();
        Date jobDate = DateUtils.getDate("2017-3-6", "yyyy-MM-dd");
        Date beforeDate = getBeforeDay(date, 0);

        for (int i=14;i<48;i++){
            Calendar c = Calendar.getInstance();
            c.set(2017, 3 - 1, 14, i,c.get(Calendar.MINUTE));
            Date runDate = c.getTime();
            System.out.println(c.get(Calendar.YEAR) + ":" + (c.get(Calendar.MONTH) + 1) + ":" + c.get(Calendar.DATE) + ":" + c.get(Calendar.HOUR_OF_DAY));
            long todayTime = beforeDate.getTime();
            long runTime = runDate.getTime();
            if ((todayTime - runTime) / (1000 * 3600) < 1) {//当前时间大约执行时间1个小时内，加入下一个小时的任务
                System.out.println("runIndex:"+i);
            } else {
                int hour = c.get(Calendar.HOUR);
                c.set(Calendar.HOUR, hour + 1);
                System.out.println("--"+c.get(Calendar.YEAR) + ":" + (c.get(Calendar.MONTH) + 1) + ":" + c.get(Calendar.DATE) + ":" + c.get(Calendar.HOUR_OF_DAY));
            }
        }
        System.out.println("date between :" + betweenDay(jobDate, beforeDate));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 2),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 3),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 4),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 5),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 6),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 7),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 8),date));
        System.out.println("date between :" + betweenDay(getBeforeDay(date, 9),date));
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(date);
        System.out.println("一年第几周"+aCalendar.get(Calendar.WEEK_OF_YEAR));

    }
}
