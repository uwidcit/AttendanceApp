package com.jevon.studentrollrecorder.utils;

import java.text.DecimalFormat;

/**
 * Created by jevon on 11-Apr-16.
 */
public final class Utils {
    public static final String COURSE_CODE = "course_code";
    public static final String COURSE_NAME = "course_name";
    public static final String SESSIONS = "sessions";

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

}
