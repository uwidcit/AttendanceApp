package com.jevon.studentrollrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jevon.studentrollrecorder.pojo.Course;
import com.jevon.studentrollrecorder.pojo.Lecture;
import com.jevon.studentrollrecorder.pojo.Student;
import com.jevon.studentrollrecorder.utils.FirebaseHelper;
import com.jevon.studentrollrecorder.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/*Service to process a scanned id.
* 1st checks if the lectures has any courses
* 2nd checks if there is a lecture at the moment
* 3rd checks if the id scanned belongs to a student registered for the course
* 4th marks the student present for the session*/
public class IdCheckService extends Service {
    private static final String TAG = "IdCheckService";
    private int currentHour;
    private String today;
    private LectureSession currentSession;
    private String scanned_id;
    private ArrayList<Course> courses;

    private final IBinder iBinder = new IdServiceBinder(this);

    public IdCheckService() {
    }

    //Entry point to processing in the service. Called from main activity when id is scanned
    public void processStudent(String student_id){
        Log.e(TAG, "running service for "+ student_id);
        currentHour = Utils.getCurrentHour();
        today = Utils.getCurrDay();
        Log.e(TAG, "today: " + today);
        scanned_id = student_id;
        courses = new ArrayList<>();
        getCourses();
    }

    //gets course code of and an ID for course going on at the moment, null otherwise
    private LectureSession getCurrentSession(){
        LectureSession currSession = null;
        for(Course c: courses){
            HashMap<String,Lecture> lectures = c.getLecturess();
            if(lectures!=null)
                for (Lecture lecture : lectures.values()) {
                    if(currentHour >= lecture.getStartHr() && currentHour < lecture.getEndHr() && lecture.getDay().equals(today)){
                        currSession = new LectureSession( c.getCourseCode(), Utils.getIDTimeStamp(lecture.getStartHr()) );
                    }
                }
        }
        return currSession;
    }

    //gets a all the courses belonging to the user and stores them in a list
    public void getCourses(){
        FirebaseHelper fh = new FirebaseHelper();
        Firebase ref_id = fh.getRef_id();
        ref_id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()){
                    Course c = postSnapshot.getValue(Course.class);
                    Log.e("Course received", c.toString());
                    courses.add(c);
                }
                if(courses.size()>0)
                    markAsPresentInFirebase();
                else
                    Toast.makeText(getApplicationContext(),"No courses found on your profile",Toast.LENGTH_LONG).show();
            }
            @Override public void onCancelled(FirebaseError error) {
                Log.e(TAG,"The read failed: " + error.getMessage());
            }
        });
    }

    /*If there is a lecture scheduled for now and the student is registered for the course record him/her as present*/
    private void markAsPresentInFirebase(){
        currentSession = getCurrentSession();
        if(currentSession!=null){
            Log.e(TAG, "current session: " + currentSession.toString());
            //we have a class now so check if student is in that class
            if(isRegistered(scanned_id, currentSession.courseCode)){
                Log.e(TAG, scanned_id + " present for "+ currentSession.toString());
                FirebaseHelper fh = new FirebaseHelper();
                fh.markAsPresent(currentSession.courseCode,currentSession.sessionID,scanned_id);
            }
            else {
                Log.e(TAG, scanned_id + " not part of " + currentSession.toString());
                Toast.makeText(getApplicationContext(), " not part of " + currentSession.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"No Class at the moment",Toast.LENGTH_LONG).show();
            Log.e(TAG, "No Class at the moment");
        }
    }

    //checks if the specified student is registered for the specified course
    private boolean isRegistered(String student_id, String course_code){
        for( Course c: courses){
            if(c.getCourseCode().equals((course_code))) {
                HashMap<String, Student> map_students = c.getStudents();
                if(map_students != null)
                    for (Student s : map_students.values()) {
                        if (s.getId().equals(student_id)) return true;
                    }
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "Service onCreate");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Service onDestroy");
    }

    //Actual session object
    private class LectureSession{
        private String sessionID;
        private String courseCode;
        private LectureSession(String courseCode, String sessionID) {
            this.courseCode = courseCode;
            this.sessionID = sessionID;
        }
        public String toString(){ return courseCode+" - "+sessionID;}
    }
}
