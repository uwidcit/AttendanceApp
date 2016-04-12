package com.jevon.studentrollrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jevon.studentrollrecorder.pojo.Student;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SCANNER = 1;
    private static final int PERMISSION_CAMERA = 2;
    private ListView lv_present_students;
    private ArrayList<Student> students;
    private ArrayAdapter<Student> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpListView();
    }



    private void setUpListView(){
        lv_present_students = (ListView) findViewById(R.id.lv_present);
        students = new ArrayList<>();
        adapter= new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,students);
        lv_present_students.setAdapter(adapter);
    }

    //Receives results of scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCANNER && resultCode == RESULT_OK) {
            String results = data.getStringExtra("results");
            Log.e("Scanned",results);
            Student s = new Student(results,"name");
            adapter.add(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_CAMERA){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(MainActivity.this, Scanner.class), REQUEST_SCANNER);
             else
                Toast.makeText(this,"Camera permissions must be granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_courses) {
            startActivity(new Intent(MainActivity.this, ViewCourses.class));
            return true;
        }
        if (id == R.id.action_add_course) {
            startActivity(new Intent(MainActivity.this, AddCourseActivity.class));
            return true;
        }
        if (id == R.id.action_clear_list) {
            adapter.clear();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void scanCode(View v){
        if(v == findViewById(R.id.fab_scan)){
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(MainActivity.this, Scanner.class), REQUEST_SCANNER);
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);
            }
        }
    }
}
