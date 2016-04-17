package com.jevon.studentrollrecorder.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jevon on 11-Apr-16.
 */

/*Utility class to reference constants and provide a few helpful methods*/
public final class Utils {
    public static final String COURSE_CODE = "courseCode";
    public static final String COURSE_NAME = "courseName";
    public static final String LECTURES = "lectures";
    public static final String SESSIONS = "sessions";
    public static final String ID = "id";
    public static final String COURSES = "courses";
    public static final String LOGGED_IN = "loggedIN";
    public static final String SHAREDPREF = "rollrecPref";
    public static final String SCANNED_ID = "scannedID";
    public static final String ATTENDEES="attendees";

    public static String formatTime(int hour, int minute){
        String am_pm = "am";
        if(hour>=12 && hour < 24){
            am_pm = "pm";
            hour = hour % 12;
        }
        if(hour == 0) hour = 12;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour)+":"+df.format(minute)+" "+am_pm;
    }

    public static String getIDTimeStamp(int startHr){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EE dd-MM-yy", Locale.ENGLISH);
        return df.format(c.getTime())+" "+startHr;
    }

    public static int getCurrentHour(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinute(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    public static String getCurrDay(){
        Calendar c = Calendar.getInstance();
        return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    public static int getMilitaryTime(int hour, int min){
        return hour*100 + min;
    }

}
