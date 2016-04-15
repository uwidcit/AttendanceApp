package com.jevon.studentrollrecorder.pojo;

/**
 * Created by jevon on 14-Apr-16.
 */

/*Not really a session object. More of an entry of a student for a session. Class necessary for FB
    to map data*/
public class Session{
    String id;

    public Session() {
    }

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}