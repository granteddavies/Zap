package com.zap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateEventActivity extends AppCompatActivity {

    private ArrayList<User> friends = new ArrayList<>();
    private FriendInviteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new FriendInviteAdapter(friends, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.inviteList);
        recyclerView.setAdapter(adapter);

        loadFriends();
    }

    private void loadFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray fbFriends = response.getJSONObject().getJSONArray("data");

                            friends.clear();
                            if (fbFriends != null) {
                                for (int i = 0; i < fbFriends.length(); i++) {
                                    JSONObject fbFriend = fbFriends.getJSONObject(i);
                                    User friend = new User(fbFriend.getString("name"),
                                            fbFriend.getString("id"));
                                    friends.add(friend);
                                }
                            }

                            // TODO: remove this test line
                            friends.add(Profile.user);
                            //

                            initializeFriends();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void initializeFriends() {
        for (int i = 0; i < friends.size(); i++) {
            final int index = i;
            final User friend = friends.get(index);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final MobileServiceList<User> result =
                                Profile.mClient.getTable(User.class).where()
                                        .field("id").eq(friend.getId())
                                        .execute().get();

                        if (result.size() == 1) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    friends.set(index, result.get(0));
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else if (result.size() == 0) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    friend.setAvailable(false);
                                    friend.setActivity(null);
                                    adapter.notifyDataSetChanged();
                                }
                            });
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(CreateEventActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
