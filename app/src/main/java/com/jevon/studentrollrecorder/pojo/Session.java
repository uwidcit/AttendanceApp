package com.jevon.studentrollrecorder.pojo;

import java.text.DecimalFormat;


/**
 * Created by jevon on 10-Apr-16.
 */
public class Session{
    private int startHr, startMin, endHr, endMin;
    private String day;

    public Session(){}

    public Session(int startHr, int startMin, int endHr, int endMin, String day) {
        this.startHr = startHr;
        this.startMin = startMin;
        this.endHr = endHr;
        this.endMin = endMin;
        this.day = day;
    }

    @Override
    public String toString() {
        return day+ "  "+ formatTime(startHr,startMin) + " - " + formatTime(endHr,endMin);
    }

    private String formatTime(int hour, int minute){
        String am_pm = "am";
        if(hour>=12 && hour < 24){
            am_pm = "pm";
            hour = hour % 12;
        }
        if(hour == 0) hour = 12;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour)+":"+df.format(minute)+" "+am_pm;
    }

    public int getStartHr() {
        return startHr;
    }

    public void setStartHr(int startHr) {
        this.startHr = startHr;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHr() {
        return endHr;
    }

    public void setEndHr(int endHr) {
        this.endHr = endHr;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
