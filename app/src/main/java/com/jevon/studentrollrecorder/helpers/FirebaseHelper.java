package com.jevon.studentrollrecorder.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.jevon.studentrollrecorder.pojo.Attendee;
import com.jevon.studentrollrecorder.pojo.Course;
import com.jevon.studentrollrecorder.pojo.Lecture;
import com.jevon.studentrollrecorder.pojo.Student;
import com.jevon.studentrollrecorder.utils.MyApplication;
import com.jevon.studentrollrecorder.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jevon on 11-Apr-16.
 */
public class FirebaseHelper {
    private String uid;
    private Firebase ref, ref_id;
    private MyApplication myApplication;

    public FirebaseHelper(){
        myApplication = MyApplication.getInstance();
        ref = myApplication.getRef();
        uid = myApplication.getSharedPreferences(Utils.SHAREDPREF, Context.MODE_PRIVATE).getString(Utils.ID,null);
        ref_id = ref.child(Utils.LECTURERS).child(uid);
    }

    public Firebase getRef(){ return ref;}

    public Firebase getRef_id() {
        return ref_id;
    }

    public void addCourse(Course course){
        ref_id.child(course.getCourseCode()).setValue(course);
    }

    public void addLectures(String courseCode, ArrayList<Lecture> lectures){
        ObjectMapper m = new ObjectMapper();
        Map<String,Object> sesMap = new HashMap<>();
        for (Lecture l : lectures) {
                try{
                    sesMap = m.convertValue(l, HashMap.class);
                    ref_id.child(courseCode).child(Utils.LECTURES).child(l.getDay().substring(0,3)+" "+l.getStartHr()).setValue(sesMap);
                    Log.e("FBH","added lecture");
                }catch (IllegalArgumentException e){e.printStackTrace();}
        }
    }

    public  void markAsPresent(String courseCode, String session_id, String student_id){
        int hour = TimeHelper.getCurrentHour();
        int minute = TimeHelper.getCurrentMinute();

        Attendee a = new Attendee(hour,minute);
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> attendeeMap = new HashMap<>();

        try {
            attendeeMap = m.convertValue(a, HashMap.class);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        ref_id.child(courseCode).child(Utils.SESSIONS).child(session_id).child(Utils.DATE).setValue(session_id);
        ref_id.child(courseCode).child(Utils.SESSIONS).child(session_id).child(Utils.ATTENDEES).child(student_id).setValue(attendeeMap);
        broadcastIntent(student_id);
    }

    public void addStudentToClass(String courseCode, String studentName, String studentID){
        Student student = new Student(studentID,studentName);
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> studentMap = new HashMap<>();

        try{
            studentMap = m.convertValue(student, HashMap.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ref_id.child(courseCode).child(Utils.STUDENTS).child(studentID).setValue(studentMap);
    }

    public String getUID(){
        return this.uid;
    }

    public void broadcastIntent(String studentId){
        Intent i = new Intent();
        i.setAction("ADD.STUDENT.TO.LISTVIEW");
        Bundle b = new Bundle();
        b.putString(Utils.ID,studentId);
        i.putExtras(b);
        Context c = myApplication.getApplicationContext();
        c.sendBroadcast(i);
    }
}
