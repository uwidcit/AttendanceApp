package com.jevon.studentrollrecorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.jevon.studentrollrecorder.pojo.User;

public class SignInActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private TextView userName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
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

        //Once the token exists, there is no need to login the user.
        sharedPreferences = this.getApplicationContext().getSharedPreferences("google_token_info", MODE_PRIVATE);
        if(sharedPreferences.contains("token")){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }

        //setting application context
        Firebase.setAndroidContext(this);

        //find the userID text view
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //userName = (TextView) findViewById(R.id.userId);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        final Context ctx = this;
        //userName.setText(result.getSignInAccount().getDisplayName());
        if (result.isSuccess()) {
            final GoogleSignInResult gsir = result;
            //create thread to obtain the authentication token which will be used for firebase login.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Signed in successfully, show authenticated UI.
                    final GoogleSignInAccount acct = gsir.getSignInAccount();;
                    String scopes = "oauth2:profile email";
                    try {
                        final String token = GoogleAuthUtil.getToken(getApplicationContext(), acct.getEmail(), scopes);
                        final Firebase ref = new Firebase("https://comp3275.firebaseio.com");
                        ref.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                // the Google user is now authenticated with your Firebase app
                                // we can now put the token into the shared preferences.
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putString("token", token);
                                editor.commit();
                                Log.i("AUTH DATA",">>>> "+authData.getUid());
                                //store user data to firebase
                                ref.child("/users/"+authData.getUid()).setValue(new User(acct.getDisplayName(), authData.getProviderData().get("profileImageURL").toString()));
                                //start main activity.
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                ctx.startActivity(intent);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                            }
                        });
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
