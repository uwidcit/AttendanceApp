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

public class ViewAnalyticsActivity extends AppCompatActivity {

    private Bundle bundle;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analytics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        listView = (ListView) findViewById(R.id.analytics_lv);
        setUpListView();
    }

    private void setUpListView(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    start(StudentAttendanceActivity.class);
                }
            }
        });
    }

    public void start(Class className){
        Intent intent = new Intent(ViewAnalyticsActivity.this, className);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}