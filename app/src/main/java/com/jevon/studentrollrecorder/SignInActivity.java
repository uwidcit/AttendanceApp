package com.jevon.studentrollrecorder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jevon.studentrollrecorder.utils.MyApplication;
import com.jevon.studentrollrecorder.utils.Utils;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int REQUEST_AUTHORIZATION = 3 ;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int PERMISSION_INTERNET = 2;
    private ProgressDialog progressDialog;
    private MyApplication myApplication;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if logged in before go to main activity
        SharedPreferences sp = getSharedPreferences(Utils.SHAREDPREF, MODE_PRIVATE);
        if(sp.getBoolean(Utils.LOGGED_IN,false))
            startActivity(new Intent(this,MainActivity.class));

        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Extending the Application allows us to access data from any activity or class
        myApplication = (MyApplication)getApplication();
        myApplication.setFireBaseRef("https://comp3275.firebaseio.com");
        ref = myApplication.getRef();

        //set up sign in button
        SignInButton sib = (SignInButton)findViewById(R.id.sign_in_button);
        if(sib!=null) sib.setOnClickListener(this);
        setUpGoogleClient();
    }

    //set up google sign-in options and the apiClient
    private void setUpGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
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

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        setUpProgressDialog();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing into Google account");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(3);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
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
                signIn();
                Log.e("SignIn","signin called again");
            } else {
                // User denied access, show him the account chooser again
                Toast.makeText(this, "You have to allow access", Toast.LENGTH_LONG).show();
            }
        }

    }

    //called after result in onActivityResult() for signIn() was received
    private void handleSignInResult(final GoogleSignInResult result) {
        final Context ctx = this;
        if (result.isSuccess()) {
            progressDialog.incrementProgressBy(1);
            progressDialog.setMessage("Getting Google Account");
            final GoogleSignInResult gsir = result;
            //create thread to obtain the authentication token which will be used for firebase login.
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = gsir.getSignInAccount();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.incrementProgressBy(1);
                            progressDialog.setMessage("Logging into account");
                        }
                    });

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
                                    progressDialog.incrementProgressBy(1);
                                    progressDialog.setMessage("Finishing up");
                                    setAsLoggedIn(authData.getUid());
                                    progressDialog.dismiss();
                                    //logged in so go to main activity
                                    ctx.startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                }
                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    Toast.makeText(SignInActivity.this,"Error Authenticating Firebase",Toast.LENGTH_LONG).show();
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

    //sets the user as logged in in sharedprefs so he won't have to log in each time the app is launched
    private void setAsLoggedIn(String uid){
        SharedPreferences sp = getSharedPreferences(Utils.SHAREDPREF, MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(Utils.LOGGED_IN,true);
        spe.putString(Utils.ID,uid);
        spe.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_INTERNET){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"try signing in again", Toast.LENGTH_LONG).show();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
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
