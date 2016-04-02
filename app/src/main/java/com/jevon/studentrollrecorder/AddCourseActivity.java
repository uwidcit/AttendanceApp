package com.jevon.studentrollrecorder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.jevon.studentrollrecorder.pojo.Course;

public class AddCourseActivity extends AppCompatActivity {

    private EditText et_code, et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_code = (EditText) findViewById(R.id.et_course_code);
        et_name = (EditText) findViewById(R.id.et_course_name);
    }

    private void saveCourse(){
        String name = et_name.getText().toString();
        String code = et_code.getText().toString();
        if(!name.matches(".*[a-zA-Z]+.*") || !code.matches(".*[a-zA-Z]+.*")){
            Snackbar.make(et_code,"You must enter a course name and course code.",Snackbar.LENGTH_LONG).show();
        }
        else {
            Course c = new Course(code,name);
            Snackbar.make(et_code,"Created "+c.toString(),Snackbar.LENGTH_INDEFINITE).show();
            et_code.setText("");
            et_name.setText("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveCourse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
