package com.jevon.studentrollrecorder.utils;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.jevon.studentrollrecorder.pojo.Attendee;
import com.jevon.studentrollrecorder.pojo.Course;
import com.jevon.studentrollrecorder.pojo.Lecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jevon on 11-Apr-16.
 */
public class FirebaseHelper {
    private String uid;
    private Firebase ref, ref_id;

    public FirebaseHelper(){
        MyApplication myApplication = MyApplication.getInstance();
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
        int hour = Utils.getCurrentHour();
        int minute = Utils.getCurrentMinute();

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
    }

    public String getUID(){
        return this.uid;
    }
}
