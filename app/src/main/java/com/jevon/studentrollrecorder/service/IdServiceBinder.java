package com.jevon.studentrollrecorder.service;

import android.os.Binder;

/**
 * Created by jevon on 13-Apr-16.
 */
public class IdServiceBinder extends Binder {
    private IdCheckService idCheckService;

    public IdServiceBinder(IdCheckService idCheckService){
        this.idCheckService = idCheckService;
    }

    public IdCheckService getService(){
        return this.idCheckService;
    }
}
