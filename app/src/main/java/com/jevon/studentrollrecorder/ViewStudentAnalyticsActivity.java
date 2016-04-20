package com.jevon.studentrollrecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jevon.studentrollrecorder.adapter.StudentsAdapter;
import com.jevon.studentrollrecorder.helpers.FirebaseHelper;
import com.jevon.studentrollrecorder.pojo.Student;
import com.jevon.studentrollrecorder.utils.Utils;

import java.util.ArrayList;

public class ViewStudentAnalyticsActivity extends AppCompatActivity {

    private String courseCode;
    private String courseName;
    private ArrayList<Student> students;
    private StudentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_analytics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            courseCode = bundle.getString(Utils.COURSE_CODE);
            courseName = bundle.getString(Utils.COURSE_NAME);
        }
        setUpRecyclerView();
        loadData();
/*        student_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 build a bundle with the course data and student data to pass to next activity.
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
        });*/
    }

    private void setUpRecyclerView(){
        RecyclerView student_list = (RecyclerView) findViewById(R.id.rv_students);
        student_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        student_list.setLayoutManager(llm);
        students = new ArrayList<>();
        adapter = new StudentsAdapter(ViewStudentAnalyticsActivity.this,students,courseCode,courseName);
        student_list.setAdapter(adapter);
    }

    private void loadData(){
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        Firebase ref_id = firebaseHelper.getRef_id();
        Firebase studentsRef = ref_id.child(courseCode).child(Utils.STUDENTS);

        /* get a list of the students registered for this course from firebase and
         add to dataset for recyclerview */
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
    }
}
