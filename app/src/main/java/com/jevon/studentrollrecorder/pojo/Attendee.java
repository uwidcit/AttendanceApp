package com.jevon.studentrollrecorder.pojo;

/**
 * Created by Shiva on 4/16/2016.
 */
public class Attendee {
    int hr, min;
    String id;

    public Attendee() {
    }

    public Attendee(int hr, int min, String id) {
        this.hr = hr;
        this.min = min;
        this.id = id;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
