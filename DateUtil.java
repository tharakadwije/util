/*
 * Copyright (c) 2016. BTGD. All Rights Reserved.
 *
 * Class                 : DateUtil.java
 * Description           : Provide a series of Date Utility APIs 
 *
 * Modification History
 * Date                    Modifier Name                Reason
 * ---------                ------------              -------------
 * 20-Sep-2016             Yang Shengong               Initial version
 *
 * @version 1.0
 * @since release 1.0
 * @author Yang Shengong
 */

package com.psa.pc.fw.ac.util;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * Class {@code DateUtil} provides a series of common Date
 * Utility APIs so that the handling associated with Date object
 * can be simplified and reused in PCP
 *
 * <p> All APIs are static method, without any object instance
 * required.
 *
 * @author Yang Shengong
 * @version 1.0
 */

public class DateUtil {
    
    /**
     * Default date format "yyyy-MM-dd HH:mm:ss" 
     * @deprecated 
     */
    public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    
    /** 
     * Default date format "yyyyMMddHHmmss" 
     */
    public static final String DATE_FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    
    /**
     * ISO8601 date format "yyyy-MM-ddTHH:mm" 
     */
    public static final String ISO8601_yyyyMMddHHmm = "yyyy-MM-dd'T'HH:mm";
    
    /**
     * ISO8601 date format "yyyy-MM-ddTHH:mm:ss" 
     */
    public static final String ISO8601_yyyyMMddHHmmss = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * ISO8601 date format "yyyy-MM-dd" 
     */
    public static final String ISO8601_yyyyMMdd = "yyyy-MM-dd";
    
    /** Private Constructor to avoid the instance of the object*/
    private DateUtil() {}
    
    /**
     * Get current datetime based on specified format
     * 
     * @param pattern Standard Datetime format
     * @return Current datetime with specified format
     */
    public static String current(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
    
    /**
     * Get current datetime based on ISO 8601 format
     * 
     * @return Current datetime with specified format
     */
    public static String currentDateTime() {
        return dateToString(new Date());
    }
    
    /**
     * Get Timestamp instance of current datetime
     * 
     * @return Current datetime
     */
    public static Timestamp current() {
        return new Timestamp(new Date().getTime());
    }
    
    /**
     * Convert java.util.Date to java.lang.String with the specified format
     * 
     * @param source Date Object to convert
     * @param pattern Specified datetime format
     * @return Converted datetime
     */
    public static String dateToString(Date source, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(source);
        } catch (Exception ex) {
            return null;
        }
//        return dateToString(source, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Convert java.util.Date to java.lang.String with the specified format
     * 
     * @param source Date Object to convert
     * @param formatter Specified datetime formatter
     * @return Converted datetime
     */
    public static String dateToString(Date source, DateTimeFormatter formatter) {
        try {
            LocalDateTime dateTime = source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();        
            return dateTime.format(formatter == null ? DateTimeFormatter.ofPattern(ISO8601_yyyyMMddHHmmss) : formatter);
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Convert java.util.Date to java.lang.String with the ISO 8601 format 
     * "yyyy-MM-ddTHH:mm:ss"
     * 
     * @param source Date Object to convert
     * @return Converted datetime
     */
    public static String dateToString(Date source) {
        return dateToString(source, ISO8601_yyyyMMddHHmmss);
    }
    
    /**
     * Convert java.lang.String to java.util.Date with the specified format
     * 
     * @param source String object to convert
     * @param pattern Specified datetime format
     * @return Convertted datetime; If datetime doesn't expected format, 
     *         return null
     */
    public static Date stringToDate(String source, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            return sdf.parse(source);
        } catch (Exception ex) {
            return null;
        }
//        return stringToDate(source, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Convert java.lang.String to java.util.Date with the specified format
     * 
     * @param source String object to convert
     * @param formatter Specified datetime format
     * @return Convertted datetime; If datetime doesn't expected format, 
     *         return null
     */
    public static Date stringToDate(String source, DateTimeFormatter formatter) {
        try {
            return Date.from(LocalDateTime.parse(source, formatter).atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Convert java.lang.String to java.util.Date with the standard ISO 8601 format
     * 
     * @param source String object to convert
     * @return Convertted datetime; If datetime doesn't expected format, 
     *         return null
     */
    public static Date stringToDate(String source) {
        return stringToDate(source, ISO8601_yyyyMMddHHmmss);
    }
    
    /**
     * Convert a datetime string from ISO8601_yyyyMMddHHmmss to specified datetime format
     * 
     * @param source String object to convert
     * @param format Specified datetime format
     * @return Converted datetime string with specified datetime format
     */
    public static String stringToString(String source, String format) {
        return stringToString(source, ISO8601_yyyyMMddHHmmss, format);
    }
    
    /**
     * Convert a datetime string from one date format to another
     * 
     * @param source String object to convert
     * @param formatFr Specified datetime format
     * @param formatTo Specified datetime format
     * @return Converted datetime string with specified datetime format
     */
    public static String stringToString(String source, String formatFr, String formatTo) {
        Date date = stringToDate(source, formatFr);
        return date == null ? null : dateToString(date, formatTo);
    }
    
    /**
     * Acquire offect datetime based on a specified time point and offset 
     * quantity. Offset unit is "DAY"
     * 
     * <p>Offset quantity is either positive or negative 
     * 
     * <p>Example:
     * <pre>
     * Date date1 = ... //2015-09-20 11:20:00
     * Date result1 = DateUtil.offset(date1, 1); 
     * //result1 = 2015-09-21 11:20:00
     * 
     * Date date2 = ... //2015-09-20 11:20:00
     * Date result2 = DateUtil.offset(date2, -4); 
     * //result2 = 2015-09-16 11:20:00
     * </pre>
     * 
     * @param dt Datum date 
     * @param count Offset quantity
     * @return Offset datetime
     * 
     * @see com.psa.pc.fw.ac.util.DateUtil.TimeUnit
     */
    public static Date offset(Date dt, int count) {
        return offset(dt, count, TimeUnit.DAY);
    }
    
    /**
     * Acquire offect datetime based on a specified time point, offset 
     * quantity and offset unit.
     * 
     * <p>Offset quantity is either positive or negative, and four offset 
     * units can be specified.
     * 
     * <p>Example:
     * <pre>
     * Date date1 = ... //2015-09-20 11:20:00
     * Date result1 = DateUtil.offset(date1, 1, TimeUnit.DAY); 
     * //result1 = 2015-09-21 11:20:00
     * 
     * Date date2 = ... //2015-09-20 11:20:00
     * Date result2 = DateUtil.offset(date2, -4, TimeUnit.HOUR); 
     * //result2 = 2015-09-16 07:20:00
     *
     * Date date3 = ... //2015-09-20 11:20:00
     * Date result3 = DateUtil.offset(date3, 18, TimeUnit.MINUTE); 
     * //result3 = 2015-09-21 11:38:00
     *
     * Date date4 = ... //2015-09-20 11:20:00
     * Date result4 = DateUtil.offset(date4, -30, TimeUnit.SECOND); 
     * //result3 = 2015-09-21 11:19:30
     * </pre>
     * 
     * @param date Datum date 
     * @param count Offset quantity
     * @param unit Offset Unit
     * @return Offset datetime
     * 
     * @see com.psa.pc.fw.ac.util.DateUtil.TimeUnit
     */
    public static Date offset(Date date, int count, TimeUnit unit) {
        if(date == null || unit == null) {
            return date;
        }
        
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();      
        if(unit.isSecond()) {
            dateTime = dateTime.plusSeconds(count);
        } else if(unit.isMinute()) {
            dateTime = dateTime.plusMinutes(count);
        } else if(unit.isHour()) {
            dateTime = dateTime.plusHours(count);
        } else if(unit.isYear()) {
            dateTime = dateTime.plusYears(count);
        } else {
            dateTime = dateTime.plusDays(count);
        }
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * To get start time point of the day: ZERO hour, ZERO minute and ZERO second, 
     * like yyyy-MM-dd 00:00:00
     * 
     * @param date Datum date
     * @return Start time point of the day
     */
    public static Date startOfDay(Date date) {
        if(date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }
    
    /**
     * To get end time point of the day: 23 hour, 59 minute and 59 second, 
     * like yyyy-MM-dd 23:59:59
     * 
     * @param date Datum date
     * @return End time point of the day
     */
    public static Date endOfDay(Date date) {
        if(date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 000);
       
        return calendar.getTime();
    }
    
    /**
     * Check whether a datetime is within the specified duration
     * 
     * <p>Duration is NOT mandatory, the checking is for specified 
     * duration date only.
     * 
     * <p>Example:
     * <pre>
     * Date date1 = ... //2015-08-20 10:30:00
     * Date startDt1 = ... //2015-08-10 20:20:10
     * Date endDt1 = ... //2015-08-25 08:30:12
     * boolean result1 = DateUtil.within(date1, startDt1, endDt1); //result1 = true
     * 
     * Date date2 = ... //2015-08-20 10:30:00
     * Date startDt2 = ... //2015-08-10 20:20:10
     * Date endDt2 = null;
     * boolean result2 = DateUtil.within(date2, startDt2, endDt2); //result2 = false
     * 
     * Date date3 = ... //2015-08-20 10:30:00
     * Date startDt3 = null;
     * Date endDt3 = ... //2015-08-25 08:30:12
     * boolean result3 = DateUtil.within(date3, startDt3, endDt3); //result3 = true
     * 
     * Date date4 = ... //2015-08-20 10:30:00
     * Date startDt4 = null;
     * Date endDt4 = null;
     * boolean result4 = DateUtil.within(date4, startDt4, endDt4); //result4 = true
     * </pre>
     * 
     * @param date Datum date
     * @param startDt Duration from
     * @param endDt Duration to
     * 
     * @return True if service date between start Date and end Date
     */
    public static boolean within(Date date, Date startDt, Date endDt) {
        if(date == null) {
            return false;
        } else {
            if(startDt == null) {
                if(endDt == null) {
                    return true;
                } else {
                    return !date.after(endDt);
                }
            } else {
                if(endDt == null) {
                    return !date.before(startDt);
                } else {
                    return !date.before(startDt) && !date.after(endDt);
                }
            }
        }
    }
    
    /**
     * Make sure a date is not null 
     * 
     * @param date Checked date
     * @return current date if input is null, otherwise it is just the input
     */
    public static Date noNull(Date date) {
        return date == null ? new Date() : date;
    }
    
    /**
     * Calculate the difference of two date time based on the DAY. 
     * <p>Both date is mandatory
     * <P>Difference = Later date time - Earlier date time
     * 
     * @param later Later date time
     * @param earlier Earlier date time
     * @return difference
     */
    public static double differ(Date later, Date earlier) {
        return differ(later, earlier, TimeUnit.DAY);
    }
    
    /**
     * Calculate the difference of two date time based on the specified Unit. 
     * <p>Both date is mandatory, and unit is optional. If unit is not provided, then default unit is DAY
     * <P>Difference = Later date time - Earlier date time
     * 
     * @param later Later date time
     * @param earlier Earlier date time
     * @param unit Unit time
     * @return difference
     */
    public static double differ(Date later, Date earlier, TimeUnit unit) {
        if(earlier == null || later == null) {
            throw new IllegalArgumentException("Arguments 'earlier' and 'later' cannot be null");
        }
        
        if(unit == null) {
            unit = TimeUnit.DAY;
        }
        
        if (unit.isYear()) {
            throw new UnsupportedOperationException("YEAR is not supported to do differ.");
        }
        
        double tmp = later.getTime() - earlier.getTime();
        if(unit.isSecond()) {
            return tmp / 1000;
        } else if(unit.isMinute()) {
            return tmp / 1000 / 60;
        } else if(unit.isHour()) {
            return tmp / 1000 / 3600;
        } else {
            return tmp / 1000 / 3600 / 24;
        }
    }
    
    public static boolean same(Date first, Date second) {
        if(first == null) {
            return second == null;
        } else {
            if(second == null) {
                return false;
            } else {
                return dateToString(first).equals(dateToString(second));
            }
        }
    }
    
    /**
     * Enum {@code TimeUnit} define four Datetime Units:
     * <p>DAY, HOUR, MINUTE and SECOND
     */
    public enum TimeUnit {
        YEAR("year"), DAY("day"), HOUR("hour"), MINUTE("minute"), SECOND("second");
        
        private String type;
        
        private TimeUnit(String type) {
            this.type = type;
        }
        
        private boolean isYear() {
            return this.equals(YEAR);
        }
        private boolean isDay() {
            return this.equals(DAY);
        }        
        private boolean isHour() {
            return this.equals(HOUR);
        }        
        private boolean isMinute() {
            return this.equals(MINUTE);
        }        
        private boolean isSecond() {
            return this.equals(SECOND);
        }
    }
}
