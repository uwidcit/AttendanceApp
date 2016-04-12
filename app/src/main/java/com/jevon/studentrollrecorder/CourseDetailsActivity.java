package com.jevon.studentrollrecorder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jevon.studentrollrecorder.pojo.Session;
import com.jevon.studentrollrecorder.utils.Constants;
import com.jevon.studentrollrecorder.utils.FirebaseHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CourseDetailsActivity extends AppCompatActivity {
    private String name;
    private String code;
    private Spinner spinner_day;
    private TextView tv_start, tv_end;
    private String day;
    private ListView lv_sessions;
    private ArrayAdapter<Session> adapter;
    private ArrayList<Session> sessions;
    private Context context;
    private FloatingActionButton fab_save;
    private int startHr = -1, startMin = -1, endHr = -1, endMin = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            code = extras.getString(Constants.COURSE_CODE);
            name = extras.getString(Constants.COURSE_NAME);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setTitle(code);
        }
        setUpFAB();
        setUpSpinner();
        setUpTextViews();
        setUpListView();

    }

    private void setUpFAB() {
        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day != null && startHr != -1 && startMin != -1 && endHr != -1 && endMin != -1){
                    if(getMilitaryTime(endHr,endMin) - getMilitaryTime(startHr,startMin) >= 100){
                        sessions.add(0,new Session(startHr,startMin,endHr,endMin,day));
                        adapter.notifyDataSetChanged();
                        resetFields();
                    }
                    else Toast.makeText(context,"Ensure that start time precede end time by at least 1 Hr",Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(context,"Select a day, start and end time",Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getMilitaryTime(int hour, int min){
        return hour*100 + min;
    }

    private void setUpListView(){
        lv_sessions = (ListView) findViewById(R.id.lv_sessions);
        //TODO: fetch data from DB, create session objects and add to adapter

        sessions = new ArrayList<>();
        adapter = new ArrayAdapter<>(context,R.layout.layout_listview_item_med,sessions);
        lv_sessions.setAdapter(adapter);
    }

    private  void  setUpTextViews(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int which;
                if(v.getId() == R.id.tv_start) which = 1;
                else which = 2;
                showTimePicker(which);
            }
        };
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_start.setOnClickListener(listener);
        tv_end.setOnClickListener(listener);
    }

    private void setUpSpinner(){
        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        final String[] days = new String[]{"Select a Day","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setPrompt("Select a day");
        spinner_day.setAdapter(adapter);

        spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    day = days[position];
                    Log.e("spinner", day);
                }
                else
                    day = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTimePicker(final int which){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        Dialog d = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(which == 1){
                    startHr = hourOfDay;
                    startMin = minute;
                    tv_start.setText(formatTime(hourOfDay,minute));
                }
                else if(which == 2) {
                    endHr = hourOfDay;
                    endMin = minute;
                    tv_end.setText(formatTime(hourOfDay, minute));
                }
            }
        }, hour, minute, false);
        d.setCancelable(true);
        d.show();
    }

    private String formatTime(int hour, int minute){
        String am_pm = "am";
        if(hour>=12 && hour < 24){
            am_pm = "pm";
            hour = hour % 12;
        }
        if(hour == 0) hour = 12;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour)+":"+df.format(minute)+" "+am_pm;
    }

    private void resetFields() {
        tv_end.setText("End");
        tv_start.setText("Start");
        spinner_day.setSelection(0);
        startHr =  startMin =  endHr =  endMin = -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_all) {
            saveAllSessions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAllSessions(){
        if(sessions.size() > 0){
            FirebaseHelper fh = new FirebaseHelper();
            fh.addSessions(code, sessions);
            Toast.makeText(context, "Successfully add "+sessions.size()+" sessions", Toast.LENGTH_LONG).show();
            adapter.clear();
        }
        else {
            Toast.makeText(context, "No sessions to be added", Toast.LENGTH_LONG).show();
        }

    }
}
