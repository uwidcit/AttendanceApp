package com.jevon.studentrollrecorder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.jevon.studentrollrecorder.pojo.Course;
import com.jevon.studentrollrecorder.pojo.Session;

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
        uid = myApplication.getUid();
        ref_id = ref.child("courses").child(uid);
    }

    public Firebase getRef_id() {
        return ref_id;
    }

    public void addCourse(Course course){
        ref_id.child(course.getCourseCode()).setValue(course);
    }

    public void addSessions(String courseCode, ArrayList<Session> sessions){
        ObjectMapper m = new ObjectMapper();
        Map<String,Object> sesMap = new HashMap<>();
        for (Session s : sessions) {
                try{
                    sesMap = m.convertValue(s, HashMap.class);
                }catch (IllegalArgumentException e){e.printStackTrace();}
            ref_id.child(courseCode).child(Utils.SESSIONS).push().setValue(sesMap);
        }
    }

}
