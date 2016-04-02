package com.jevon.studentrollrecorder.pojo;

/**
 * Created by jevon on 31-Mar-16.
 */
public class Course {
    private String courseName;
    private String courseCode;
    private String lecturer;

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getLecturer() {
        return lecturer;
    }

    @Override
    public String toString() {
        return "Course{ " +
                "courseName='" + courseName +
                ",  courseCode='" + courseCode + '}';
    }
}
