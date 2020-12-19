package com.atlaaya.evdrecharge.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangeDateFormat {

       /*  Date
    EEE  = Mon
    EEEE  = Monday
    dd  = 10
    MM  = 08
    MMM  = Aug
    MMMM  = August
    yy  = 91
    yyyy  = 1991
    HH  = 24 hours 01-23
    H  = 24 hours 1-23
    hh  = 12 hours 01-12
    h  = 12 hours 1-12
    mm  = minutes 01-59
    m  = minutes 1-59
    ss  = seconds 01-59
    s  = seconds 1-59
    hh:mm aa = 10:10 AM or  10:10 PM
    HH:mm  = 10:10 or 22:10

    "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
    "hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
    "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
    "yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
    "yyMMddHHmmssZ"-------------------- 010704120856-0700
    "K:mm a, z" ----------------------- 0:08 PM, PDT
    "h:mm a" -------------------------- 12:08 PM
    "EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01


    */


    /**
     * Get Date from Millisecond in format that you want
     *
     * @param datetime_format_you_want Eg. MMM dd, yyyy
     */

    public static String getDateTimeFromMillisecond(String datetime_format_you_want, long timeMillis) {
        SimpleDateFormat f = new SimpleDateFormat(datetime_format_you_want, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return f.format(calendar.getTime());
    }


    //method get milliseconds from Date time with fromat as we want. Eg yyyy-mm-dd hh:mm aa
    public static long getMilliSecFromDateTime(String date_time, String date_time_format) {

        SimpleDateFormat df = new SimpleDateFormat(date_time_format, Locale.US);
        Date new_date;
        long time = 0L;
        try {
            new_date = df.parse(date_time);
            time = new_date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;

    }

    // change date formatin portugues to 12,Aug 13 from 2013-08-12 12:23:21
    public static String getDateFromDateTimePortugues(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        Date new_date;
        long t = 0L;
        final Locale myLocale = new Locale("pt", "PT");
        SimpleDateFormat f = new SimpleDateFormat("dd, MMM yy, hh:mm", myLocale);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    // change time format from 12:33:00 to  12:33 PM
    public static String getTimeForamteHHMMA(String time) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("hh:mm aa", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * change time format from 12:33:00 PM to  12:33 PM
     *
     * @param time is time in string format
     */
    public static String getTimehhmmssAMPMtohhmmAMPM(String time) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss aa", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * change time format from 6:33 PM to  06:33 PM
     *
     * @param time is time in string format
     */
    public static String getTimehmmAMPMtohhmmAMPM(String time) {
        SimpleDateFormat df = new SimpleDateFormat("h:mm aa", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("hh:mm aa", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * change time format from 13:0 to  12:33 PM
     */
    public static String getTimeHHMMA(String time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:m", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("hh:mm aa", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get hour from 03:45 PM to 15 in 24 hour formated
     */
    public static String getHourinto24FromAMPMTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("HH", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get minute from 03:45 PM to 45 in 24 hour formated
     */
    public static String getMinuteinto24FromAMPMTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("m", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(time);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    // change date time format in  to 12,Aug 13 from 2013-08-12 12:23:21
    public static String getDateTimeMonthAM(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("MMM dd, yyyy  hh:mm aa", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get Date with Day of that date like yyyy-MM-dd to Monday, 22 June, 2015
     */
    public static String getDateWithDayName(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get Year from Date like 2015-06-26 to 2015
     */
    public static String getYearFromDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get Month from Date like 2015-06-26 to 06
     */
    public static String getMonthFromDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }

    /**
     * Get Day from Date like 2015-06-26 to 26
     */
    public static String getDayFromDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat("dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
        return s;
    }


    /**
     * Get Date from one format to other
     * Eg. 2016-28-03 to 28 March, 2016
     */

    public static String getDateFormatFromOneToOther(String date, String your_date_format, String result_date_format) {
        SimpleDateFormat df = new SimpleDateFormat(your_date_format,Locale.US);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat(result_date_format, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
//        Log.d("date:- ", "" + date);
//        Log.d("your_date_format:- ", "" + your_date_format);
//        Log.d("result_date_format:- ", "" + s);
//        s = s.replace("..", ".");

        return s;
    }
    public static String getDateFormatFromOneToOther1(String date, String your_date_format, String result_date_format) {
        SimpleDateFormat df = new SimpleDateFormat(your_date_format,Locale.FRENCH);
        Date new_date;
        long t = 0L;
        SimpleDateFormat f = new SimpleDateFormat(result_date_format, Locale.FRENCH);
        Calendar calendar = Calendar.getInstance();
        try {
            new_date = df.parse(date);
            t = new_date.getTime();
            calendar.setTimeInMillis(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = f.format(calendar.getTime());
//        Log.d("date:- ", "" + date);
//        Log.d("your_date_format:- ", "" + your_date_format);
//        Log.d("result_date_format:- ", "" + s);
//        s = s.replace("..", ".");

        return s;
    }


}
