package com.jevon.studentrollrecorder.pojo;

import java.util.HashMap;

/**
 * Created by jevon on 31-Mar-16.
 */
public class Course {
    private String courseName;
    private String courseCode;
    private HashMap<String,Session> sessions;

    public Course(){}



    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        sessions = new HashMap<>();

    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public HashMap<String,Session> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String,Session> sessions) {
        this.sessions = sessions;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }


    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
