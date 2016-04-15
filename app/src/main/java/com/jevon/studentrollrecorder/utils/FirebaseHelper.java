package com.jevon.studentrollrecorder.utils;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
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
        ref_id = ref.child(Utils.COURSES).child(uid);
    }

    public Firebase getRef_id() {
        return ref_id;
    }

    public void addCourse(Course course){
        ref_id.child(course.getCourseCode()).setValue(course);
    }

    public void addLectures(String courseCode, ArrayList<Lecture> lectures){
        ObjectMapper m = new ObjectMapper();
        Map<String,Object> sesMap = new HashMap<>();
        for (Lecture s : lectures) {
                try{
                    sesMap = m.convertValue(s, HashMap.class);
                }catch (IllegalArgumentException e){e.printStackTrace();}
            ref_id.child(courseCode).child(Utils.LECTURES).push().setValue(sesMap);
        }
    }

    public  void markAsPresent(String courseCode, String session_id, String student_id){
        ref_id.child(courseCode).child(Utils.SESSIONS).child(session_id).child(Utils.ID).setValue(student_id);
    }

}