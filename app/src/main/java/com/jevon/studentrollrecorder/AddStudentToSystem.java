package com.jevon.studentrollrecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jevon.studentrollrecorder.utils.FirebaseHelper;
import com.jevon.studentrollrecorder.utils.Utils;

public class AddStudentToSystem extends AppCompatActivity {
    private String userID, courseCode, sessionID;
    private EditText studentNameET;
    private TextView studentIDTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_system);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        loadInfoFromBundle();
        setupViews();
        setStudentID();
    }

    public void loadInfoFromBundle(){
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        userID = extras.getString("userId");
        courseCode = extras.getString("courseCode");
        sessionID = extras.getString("sessionID");
    }

    public void setupViews(){
        studentIDTV = (TextView) findViewById(R.id.tv_studentIDField);
        studentNameET = (EditText)findViewById(R.id.et_StudentName);
    }

    public void setStudentID(){
        studentIDTV.setText(userID);
    }

    public void addStudentToSystem(){
        SharedPreferences sp = getSharedPreferences(Utils.SHAREDPREF, MODE_PRIVATE);
        String teacherID = sp.getString(Utils.ID,"noUser");

        if(!teacherID.equals("noUser")) {
            String studentName = studentNameET.getText().toString();
            FirebaseHelper fh = new FirebaseHelper();
            fh.markAsPresent(courseCode, sessionID, userID);
            fh.addStudentToClass(courseCode, studentName, userID,teacherID);
        }
    }

}
