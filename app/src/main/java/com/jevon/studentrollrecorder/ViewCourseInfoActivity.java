package com.jevon.studentrollrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ViewCourseInfoActivity extends AppCompatActivity {

    private Bundle bundle;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        listView = (ListView) findViewById(R.id.course_info_list);
        setUpListView();
    }

    private void setUpListView(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    start(ViewLecturesActivity.class);
                }

                else if(position == 1){
                    start(ViewAnalyticsActivity.class);
                }

                else if(position == 2){
                    start(ViewStudentAnalyticsActivity.class);
                }
            }
        });
    }

    public void start(Class className){
        Intent intent = new Intent(ViewCourseInfoActivity.this, className);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
