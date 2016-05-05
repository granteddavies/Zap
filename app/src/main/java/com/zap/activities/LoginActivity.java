package com.zap.activities;

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
import com.zap.R;
import com.zap.helpers.DialogHelper;
import com.zap.models.Session;
import com.zap.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize facebook context
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        Session.instantiateBackendClient(this);

        // Check if the user is already logged in, if so go ahead with login logic,
        // otherwise setup the login button.
        if (Session.isFacebookSessionValid()) {
            doLogin();
        }
        else {
            initializeLoginButton();
        }
    }

    /**
     * Sets up the login button to do start app login logic if facebook login was successful.
     */
    private void initializeLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                doLogin();
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

        // Set up the callback manager to handle returns from facebook login
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * App login logic
     */
    private void doLogin() {

        // Show progress dialog
        DialogHelper.buildAndShowProgressDialog(this, getString(R.string.loader_title),
                getString(R.string.loader_text));

        // Get user from facebook and store in session
        // Then initialize the user with the app backend
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jObj = response.getJSONObject();
                            Session.user = new User(jObj.getString("name"), jObj.getString("id"));
                            initializeUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     * Initialize user data from the backend,
     */
    public void initializeUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<User> result =
                            Session.mClient.getTable(User.class).where()
                                    .field("id").eq(Session.user.getId())
                                    .execute().get();

                    // If user not in backend, initialize values and insert into backend and start
                    // the main activity
                    if (result.size() == 0) {
                        Session.user.setAvailable(false);
                        Session.user.setActivity(null);
                        insertUser();
                    }
                    // If user found, start the main activity
                    else if (result.size() == 1) {
                        Session.user = result.get(0);
                        startMainActivity();
                    }
                    else {
                        // TODO: Proper error handling
                        throw new RuntimeException("Unexpected number of matches for profile user");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }


    /**
     * Inserts the logged in user into the backend then starts the main activity
     */
    public void insertUser() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Session.mClient.getTable(User.class).insert(Session.user).get();
                    startMainActivity();
                } catch (Exception e) {
                    // TODO: Proper error handling
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Starts the main activity (forced to run on UI thread
     */
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
}
