package com.jevon.studentrollrecorder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jevon.studentrollrecorder.utils.FirebaseHelper;

public class AddStudentToSystem extends AppCompatActivity {
    private EditText userNameET;
    private TextView userIDTV;
    private String studentID, courseID, sessionID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_system);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        loadStudentInfo();
        setupViews();
    }

    public void loadStudentInfo(){
        Bundle studentData = getIntent().getExtras();
        studentID = studentData.get("userID").toString();
        courseID = studentData.get("courseCode").toString();
        sessionID = studentData.get("sessionID").toString();
    }

    public void setupViews(){
        userIDTV = (TextView)findViewById(R.id.tv_studentIDField);
        userNameET = (EditText)findViewById(R.id.et_StudentName);
    }

    public void addStudentToSystem(View view){
        String userName = userNameET.getText().toString();

        FirebaseHelper fh = new FirebaseHelper();
        fh.markAsPresent(courseID, sessionID, studentID);
        fh.addStudentToClass(courseID,userName,studentID);
    }

}