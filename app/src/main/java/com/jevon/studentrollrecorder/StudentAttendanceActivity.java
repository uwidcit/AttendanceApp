package com.jevon.studentrollrecorder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.jevon.studentrollrecorder.pojo.Session;
import com.jevon.studentrollrecorder.pojo.SortByDate;
import com.jevon.studentrollrecorder.utils.FirebaseHelper;
import com.jevon.studentrollrecorder.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class StudentAttendanceActivity extends AppCompatActivity implements OnChartValueSelectedListener{

    private LineChart lineChart;
    private FirebaseHelper firebaseHelper;
    private Firebase ref;
    private Firebase studentRef;
    private Firebase sessionRef;
    private String courseCode;
    private String courseName;
    private long numStudents=0;
    private ArrayList<Session> sessions;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create a new FirebaseHelper instance
        firebaseHelper = new FirebaseHelper();

        // set ref to point at the at this lecturers courses
        ref = firebaseHelper.getRef_id();

        // access data passed via bundle
        Bundle bundle = getIntent().getExtras();

        // pull out the course code and name

        if(bundle.containsKey(Utils.COURSE_CODE)){
            courseCode = bundle.getString(Utils.COURSE_CODE);
        }

        if(bundle.containsKey(Utils.COURSE_NAME)){
            courseName = bundle.getString(Utils.COURSE_NAME);
        }

        lineChart = (LineChart) findViewById(R.id.std_attend_line_chart);

        numStudentInCourse();
    }

    // obtain the number of students in this particular course so that chart can be set up properly
    private void numStudentInCourse(){
        studentRef = ref.child(courseCode).child(Utils.STUDENTS);

        // add a listener to student location in firebase that will be triggered when data there changes. when triggered the value of the data will be accessible
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numStudents = dataSnapshot.getChildrenCount();

                // we set up the line chart after we have received the number of students in the course
                setUpLineChart();
                getSessions();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getSessions(){

        sessionRef = ref.child(courseCode).child(Utils.SESSIONS);
        sessions = new ArrayList<>();

        // add a listener to session location in firebase that will be triggered when data there changes. when triggered the value of the data will be accessible
        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sesSnapshot: dataSnapshot.getChildren()) {
                    Session temp = sesSnapshot.getValue(Session.class);
                    sessions.add(temp);
                }

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        populateLineChart();
//                        lineChart.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                lineChart.invalidate();
//                            }
//                        });
//                    }
//                }).start();
                populateLineChart();

                sessions.clear();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // this method will create the data points on the line chart from the sessions data
    private void createEntries(){
        int xCord=0;

        for(Session s : sessions){
            // add label of string version of date of this session
            labels.add(s.getDate());

            // add entry indicating number of students present at each session
            entries.add(new Entry((float)s.getAttendees().size(), xCord++));
        }
    }

    private void populateLineChart(){

        entries = new ArrayList<>();
        labels = new ArrayList<>();

        Collections.sort(sessions, new SortByDate());

        createEntries();

        LineDataSet dataset = new LineDataSet(entries, "Number of Students Present");
        dataset.setDrawCircles(true);
        dataset.setDrawCubic(true);
        dataset.setColor(Color.BLUE);
        dataset.setCircleColor(Color.RED);

        LineData data = new LineData(labels, dataset);
        data.setValueTextColor(Color.BLUE);

        data.notifyDataChanged();

        //add data
        lineChart.setData(data);
        //let the chart know its data has changed
        lineChart.notifyDataSetChanged();

        // refresh chart
        lineChart.animateXY(1000,1000);
        //lineChart.invalidate();
    }

    private void setUpLineChart(){

        lineChart.setOnChartValueSelectedListener(this);

        // descriptive text to explain to the user why there is no chart available
        lineChart.setNoDataTextDescription("No Student Attendance Data Available.");
        // descriptive text that appears in the bottom right corner of the chart
        lineChart.setDescription("Student Attendance");
        lineChart.setDescriptionColor(Color.WHITE);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        lineChart.setDrawGridBackground(true);

        // both axes can be scaled simultaneously on zoom
        lineChart.setPinchZoom(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.GREEN);

        // add empty data
        lineChart.setData(data);

        // get the legend
        Legend legend = lineChart.getLegend();

        // modify the legend
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextColor(Color.WHITE);

        // set up Y axis

        YAxis leftAxis = lineChart.getAxisLeft();
        // set the max value on Y axis to the number of students in the course
        leftAxis.setAxisMaxValue((float)numStudents);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(Color.WHITE);

        // indicate the number of students registered for course as a line on the y axis
        LimitLine maxStudents = new LimitLine((float)(numStudents), "# registered");
        maxStudents.setLineColor(Color.RED);
        maxStudents.setTextColor(Color.RED);
        maxStudents.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        maxStudents.setTextSize(5);
        leftAxis.addLimitLine(maxStudents);

        YAxis rightAxis = lineChart.getAxisRight();
        // do not draw this
        rightAxis.setEnabled(false);

        // set up X axis
        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}