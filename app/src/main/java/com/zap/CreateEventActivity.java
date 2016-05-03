package com.zap;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private ArrayList<User> friends = new ArrayList<>();
    private FriendInviteAdapter adapter;
    private EditText editTitle, editDescription, editStartTime, editEndTime;
    private Button buttonSubmit;
    private Date startTime, endTime;
    private RecyclerView inviteList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = (EditText) findViewById(R.id.newTitleEdit);
        editDescription = (EditText) findViewById(R.id.newDescriptionEdit);

        editStartTime = (EditText) findViewById(R.id.newStartTimeEdit);
        editStartTime.setInputType(InputType.TYPE_NULL);
        editStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeKeyboard();

                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(CreateEventActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    String formatString = "";
                                    if (hourOfDay < 10) {
                                        formatString = formatString + "k";
                                    } else {
                                        formatString = formatString + "kk";
                                    }
                                    if (minute < 10) {
                                        formatString = formatString + "m";
                                    } else {
                                        formatString = formatString + "mm";
                                    }
                                    SimpleDateFormat sdf = new SimpleDateFormat(formatString);

                                    try {
                                        startTime = sdf.parse("" + hourOfDay + "" + minute);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    sdf = new SimpleDateFormat("h:mm a");
                                    editStartTime.setText(sdf.format(startTime));
                                }
                            }, hour, minute, false);
                    tpd.show();
                }
            }
        });

        editEndTime = (EditText) findViewById(R.id.newEndTimeEdit);
        editEndTime.setInputType(InputType.TYPE_NULL);
        editEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeKeyboard();

                    Calendar c = Calendar.getInstance();
                    final int hour = c.get(Calendar.HOUR_OF_DAY);
                    final int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(CreateEventActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    String formatString = "";
                                    if (hourOfDay < 10) {
                                        formatString = formatString + "k";
                                    } else {
                                        formatString = formatString + "kk";
                                    }
                                    if (minute < 10) {
                                        formatString = formatString + "m";
                                    } else {
                                        formatString = formatString + "mm";
                                    }
                                    SimpleDateFormat sdf = new SimpleDateFormat(formatString);

                                    try {
                                        endTime = sdf.parse("" + hourOfDay + "" + minute);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    sdf = new SimpleDateFormat("h:mm a");
                                    editEndTime.setText(sdf.format(endTime));
                                }
                            }, hour, minute, false);
                    tpd.show();
                }
            }
        });

        buttonSubmit = (Button) findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTitle.getText() == null || editTitle.getText().equals("")) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.no_title, Snackbar.LENGTH_LONG)
                            .show();
                }
                else {
                    submitEvent();
                }
            }
        });

        adapter = new FriendInviteAdapter(friends, this);

        inviteList = (RecyclerView) findViewById(R.id.inviteList).findViewById(R.id.friendInviteList);
        inviteList.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(R.id.inviteList).findViewById(R.id.createProgress);

        loadFriends();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void submitEvent() {
        final Event event = new Event(Profile.user.getId(), Profile.user.getName(), editTitle.getText().toString(),
                        editDescription.getText().toString(), startTime, endTime);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Profile.mClient.getTable(Event.class).insert(event).get();
                    submitInvites(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void submitInvites(Event event) {
        for (int i = 0; i < friends.size(); i++) {
            CheckBox chk = (CheckBox) inviteList.getChildAt(i).findViewById(R.id.friendCheck);
            if (chk.isChecked()) {
                final Invite invite = new Invite(event.getId(), friends.get(i).getId(), friends.get(i).getName());
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Profile.mClient.getTable(Invite.class).insert(invite).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        }

        final Invite invite = new Invite(event.getId(), Profile.user.getId(), Profile.user.getName());
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Profile.mClient.getTable(Invite.class).insert(invite).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        finish();
    }

    private void loadFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        ArrayList<User> tmpFriends = new ArrayList<>();
                        try {
                            JSONArray fbFriends = response.getJSONObject().getJSONArray("data");

                            friends.clear();
                            if (fbFriends != null) {
                                for (int i = 0; i < fbFriends.length(); i++) {
                                    JSONObject fbFriend = fbFriends.getJSONObject(i);
                                    User friend = new User(fbFriend.getString("name"),
                                            fbFriend.getString("id"));
                                    tmpFriends.add(friend);
                                }
                            }

                            initializeFriends(tmpFriends);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void initializeFriends(ArrayList<User> fbFriends) {
        friends.clear();
        for (int i = 0; i < fbFriends.size(); i++) {
            final int index = i;
            final User friend = fbFriends.get(index);
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
                                    if (result.get(0).getAvailable()) {
                                        friends.add(result.get(0));
                                    }
                                    progressBar.setVisibility(View.GONE);
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
