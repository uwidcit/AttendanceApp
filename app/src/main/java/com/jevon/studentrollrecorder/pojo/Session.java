package com.jevon.studentrollrecorder.pojo;

import com.jevon.studentrollrecorder.utils.Utils;


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
        return day+ "  "+ Utils.formatTime(startHr,startMin) + " - " + Utils.formatTime(endHr,endMin);
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
