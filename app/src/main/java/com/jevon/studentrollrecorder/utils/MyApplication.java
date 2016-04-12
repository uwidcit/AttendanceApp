package com.jevon.studentrollrecorder.utils;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by jevon on 11-Apr-16.
 */
public class MyApplication extends Application {
    private static MyApplication mApplication;
    private Firebase ref;
    private String uid;


    public static MyApplication getInstance(){
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //setting application context
        Firebase.setAndroidContext(this);
    }

    public void setFireBaseRef(String url){
        this.ref = new Firebase(url);
    }

    public Firebase getRef(){
        return this.ref;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
