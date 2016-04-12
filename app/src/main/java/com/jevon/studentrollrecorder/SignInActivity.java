package com.jevon.studentrollrecorder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jevon.studentrollrecorder.utils.MyApplication;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int REQUEST_AUTHORIZATION = 3 ;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int PERMISSION_INTERNET = 2;
    private TextView tv_title;
    private ProgressDialog progressDialog;
    private MyApplication myApplication;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myApplication = (MyApplication)getApplicationContext();
        myApplication.setFireBaseRef("https://comp3275.firebaseio.com");
        ref = myApplication.getRef();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);

        //set up google sign-in options and the apiClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else if(requestCode == REQUEST_AUTHORIZATION){
            if (resultCode == Activity.RESULT_OK) {
                // App is authorized, you can go back to sending the API request
//                Toast.makeText(this, "try signing in again", Toast.LENGTH_LONG).show();
                signIn();
            } else {
                // User denied access, show him the account chooser again
                Toast.makeText(this, "You have to allow access", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In. Please wait. ");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(final GoogleSignInResult result) {
        final Context ctx = this;
//        userName.setText(result.getSignInAccount().getDisplayName());
        if (result.isSuccess()) {
            final GoogleSignInResult gsir = result;
            //create thread to obtain the authentication token which will be used for firebase login.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = gsir.getSignInAccount();
                    String scopes = "oauth2:profile email";
                    try {
                        if (ContextCompat.checkSelfPermission(SignInActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                            progressDialog.dismiss();
                            ActivityCompat.requestPermissions(SignInActivity.this,
                                    new String[]{Manifest.permission.INTERNET},
                                    PERMISSION_INTERNET);
                        }
                        else {
                            final String token = GoogleAuthUtil.getToken(getApplicationContext(), acct.getEmail(), scopes);
                            ref.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {
                                    // the Google user is now authenticated with your Firebase app
                                    //NEED TO PUT AUTH TOKEN INTO BUNDLE OR EITHER PUT DATA IN FIREBASE HERE.
                                    myApplication.setUid(authData.getUid());
                                    Snackbar.make(tv_title,"Signed in as: " + result.getSignInAccount().getDisplayName(),Snackbar.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    ctx.startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    // there was an error
                                }
                            });
                        }
                    }catch (UserRecoverableAuthException er){
                        startActivityForResult(er.getIntent(), REQUEST_AUTHORIZATION);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_INTERNET){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"try signing in again", Toast.LENGTH_LONG).show();
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
            else
                Toast.makeText(this,"Internet permissions must be granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

}
