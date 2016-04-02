package com.jevon.studentrollrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SCANNER = 1;
    private static final int PERMISSION_CAMERA = 2;
    private TextView tv_res;
    private FloatingActionButton fab_scan;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_res = (TextView) findViewById(R.id.tv_res);

        fab_scan = (FloatingActionButton) findViewById(R.id.fab_scan);

    }

    public void GoToActivity(View v){
        if(v == findViewById(R.id.btn_add_course)){
            startActivity(new Intent(MainActivity.this,AddCourseActivity.class));
        }
        else if(v == findViewById(R.id.fab_scan)){
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(MainActivity.this, Scanner.class), REQUEST_SCANNER);
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_CAMERA){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                startActivityForResult(new Intent(MainActivity.this, Scanner.class), REQUEST_SCANNER);
             else
                Toast.makeText(this,"Camera permissions must be granted",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCANNER && resultCode == RESULT_OK) {
            String results = data.getStringExtra("results");
            tv_res.setText(results);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
