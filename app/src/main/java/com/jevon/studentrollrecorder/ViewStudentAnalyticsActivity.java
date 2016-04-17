package com.jevon.studentrollrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jevon.studentrollrecorder.pojo.Student;
import com.jevon.studentrollrecorder.utils.FirebaseHelper;
import com.jevon.studentrollrecorder.utils.Utils;

import java.util.ArrayList;

public class ViewStudentAnalyticsActivity extends AppCompatActivity {

    private String courseCode;
    private String courseName;
    private ArrayList<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_analytics);
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

        /* create a new arraylist of students */
        students = new ArrayList<>();

        /* get list view from ui */
        ListView student_list = (ListView) findViewById(R.id.list_students);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, students);
        student_list.setAdapter(adapter);

        /* get the course name and course code.  */
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            courseCode = bundle.getString(Utils.COURSE_CODE);
            courseName = bundle.getString(Utils.COURSE_NAME);
        }

        /* create a new firebase helper. */
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        /* get the root of the json tree. */
        Firebase ref = firebaseHelper.getRef();

        /* construct a ref to narrow down to students for selected course.*/
        Firebase studentsRef = ref.child(Utils.COURSES).child(firebaseHelper.getUID()).child(courseCode).child(Utils.STUDENTS);

        /* get a list of the students registered for this course from firebase and build an arraylist for display in listview */
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* clear the old students list. */
                students.clear();

                for(DataSnapshot studentSnapshot : dataSnapshot.getChildren()){
                    Student student = studentSnapshot.getValue(Student.class);
                    students.add(new Student(student.getId(), student.getName()));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        student_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* build a bundle with the course data and student data to pass to next activity. */
                Student student = students.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("studentName", student.getName());
                bundle.putString("studentId", student.getId());

                bundle.putString(Utils.COURSE_CODE, courseCode);
                bundle.putString(Utils.COURSE_NAME, courseName);

                Intent intent = new Intent(ViewStudentAnalyticsActivity.this, ViewIndividualStudentAnalytics.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
