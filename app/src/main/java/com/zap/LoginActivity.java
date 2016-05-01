package com.zap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Profile.mClient = new MobileServiceClient(
                    "https://zap.azure-mobile.net/",
                    "gOXIOvUOmvTWGEknrVggIjHFdmzycc64",
                    getApplicationContext()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);



        // Check if the user is already logged in
        if (AccessToken.getCurrentAccessToken() != null) {
            loadProfile();
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //Trying to add the spinner in onSuccess
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadProfile();
            }

            @Override
            public void onCancel() {
                // Do nothing
            }

            @Override
            public void onError(FacebookException error) {
                // Do nothing
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    private void startMainActivity() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*
        The progress dialog has been added for the load profile step. Implemented functionally

        Bug is we don't currently dismiss the progress bar, although it does disappear when you go to the main activity
        When I tried to dismiss in this method after the async execute, the app would not load a profile. I could still
        log in and out but it wouldn't go to the main activity.

     */
    private void loadProfile() {


        ProgressDialog progress = new ProgressDialog(LoginActivity.this);//parameter??
        progress.setTitle("Loading");
        progress.setMessage("Please wait while the server authenticates your login");
        progress.show();

        //progress.dismiss();


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jObj = response.getJSONObject();

                            Profile.user = new User(jObj.getString("name"), jObj.getString("id"));

                            initializeProfile();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void initializeProfile() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<User> result =
                            Profile.mClient.getTable(User.class).where()
                                    .field("id").eq(Profile.user.getId())
                                    .execute().get();

                    if (result.size() == 0) {
                        Profile.user.setAvailable(false);
                        Profile.user.setActivity(null);
                        insertUser();
                    }
                    else if (result.size() == 1) {
                        Profile.user = result.get(0);
                        startMainActivity();
                    }
                    else {
                        throw new RuntimeException("Unexpected number of matches for profile user");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public void insertUser() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Profile.mClient.getTable(User.class).insert(Profile.user).get();
                    startMainActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
