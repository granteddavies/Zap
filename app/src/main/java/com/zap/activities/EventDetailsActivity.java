package com.zap.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.zap.adapters.InviteAdapter;
import com.zap.R;
import com.zap.models.Event;
import com.zap.models.EventData;
import com.zap.models.Invite;
import com.zap.models.Session;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String ARGUMENT_EVENT_ID = "eventID";

    private TextView hostText, titleText, startTimeText, endTimeText, descriptionText, otherMembersText;
    private ToggleButton toggleYes, toggleMaybe, toggleCant;
    private EventData eventData;
    private ArrayList<Invite> invites = new ArrayList<>();
    private InviteAdapter adapter;
    private Invite userInvite;
    private ProgressBar progressBar;
    private int numTasks, numCompleteTasks;
    private Menu menu;
    private RecyclerView recyclerView;

    private boolean ignoreButtonChecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        if (Session.user == null || AccessToken.getCurrentAccessToken() == null) {
            Intent intent = new Intent(EventDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.content_event).findViewById(R.id.eventDetailsProgress);

        hostText = (TextView) findViewById(R.id.content_event).findViewById(R.id.hostText);
        titleText = (TextView) findViewById(R.id.content_event).findViewById(R.id.titleText);
        startTimeText = (TextView) findViewById(R.id.content_event).findViewById(R.id.startTimeText);
        endTimeText = (TextView) findViewById(R.id.content_event).findViewById(R.id.endTimeText);
        descriptionText = (TextView) findViewById(R.id.content_event).findViewById(R.id.descriptionText);
        otherMembersText = (TextView) findViewById(R.id.content_event).findViewById(R.id.otherMembersText);

        toggleYes = (ToggleButton) findViewById(R.id.content_event).findViewById(R.id.toggleYes);
        toggleMaybe = (ToggleButton) findViewById(R.id.content_event).findViewById(R.id.toggleMaybe);
        toggleCant = (ToggleButton) findViewById(R.id.content_event).findViewById(R.id.toggleCant);

        otherMembersText.setVisibility(View.GONE);
        toggleYes.setVisibility(View.GONE);
        toggleMaybe.setVisibility(View.GONE);
        toggleCant.setVisibility(View.GONE);

        ignoreButtonChecks = false;

        toggleYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ignoreButtonChecks) {
                    return;
                }

                if (isChecked) {
                    updateStatus(EventData.STATUS_YES);
                }
                else {
                    updateStatus(EventData.STATUS_PENDING);
                }
            }
        });

        toggleMaybe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ignoreButtonChecks) {
                    return;
                }

                if (isChecked) {
                    updateStatus(EventData.STATUS_MAYBE);
                }
                else {
                    updateStatus(EventData.STATUS_PENDING);
                }
            }
        });

        toggleCant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ignoreButtonChecks) {
                    return;
                }

                if (isChecked) {
                    updateStatus(EventData.STATUS_CANT);
                }
                else {
                    updateStatus(EventData.STATUS_PENDING);
                }
            }
        });

        adapter = new InviteAdapter(invites, this);

        recyclerView = (RecyclerView) findViewById(R.id.otherMembersList);
        recyclerView.setAdapter(adapter);

        loadEventData(getIntent().getExtras().getString(ARGUMENT_EVENT_ID));
    }

    private void loadEventData(final String eventID) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<Event> result =
                            Session.mClient.getTable(Event.class).where()
                                    .field("id").eq(eventID)
                                    .execute().get();

                    if (result.size() == 1) {
                        eventData = new EventData(result.get(0));
                    }
                    else {
                        throw new RuntimeException("Unexpected number of matches for profile user");
                    }

                    loadInvites();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void loadInvites() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<Invite> result =
                            Session.mClient.getTable(Invite.class).where()
                                    .field("eventid").eq(eventData.getEvent().getId())
                                    .execute().get();

                    invites.clear();
                    for (int i = 0; i < result.size(); i++) {
                        Invite invite = result.get(i);
                        if (invite.getRecipientid().equals(Session.user.getId())) {
                            userInvite = invite;
                        }
                        else {
                            if (!invite.getRecipientid().equals(eventData.getEvent().getHostid())) {
                                invites.add(invite);
                            }
                        }
                    }

                    showData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void showData() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                hostText.setText(eventData.getEvent().getHostname());
                titleText.setText(eventData.getEvent().getTitle());

                DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);

                Date startTime, endTime;
                startTime = eventData.getEvent().getStarttime();
                endTime = eventData.getEvent().getEndtime();

                if (startTime == null) {
                    startTimeText.setVisibility(View.GONE);
                }
                else {
                    startTimeText.setText("At " + df.format(startTime));
                }

                if (endTime == null) {
                    endTimeText.setVisibility(View.GONE);
                }
                else {
                    endTimeText.setText("Expires at " + df.format(endTime));
                }

                descriptionText.setText(eventData.getEvent().getDescription());

                progressBar.setVisibility(View.GONE);

                otherMembersText.setVisibility(View.VISIBLE);
                toggleYes.setVisibility(View.VISIBLE);
                toggleMaybe.setVisibility(View.VISIBLE);
                toggleCant.setVisibility(View.VISIBLE);

                if (invites.isEmpty()) {
                    otherMembersText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }

                inflateDeleteOption();
                updateButtonUI(userInvite.getStatus());
            }
        });
    }

    private void inflateDeleteOption() {
        if (Session.user.getId().equals(eventData.getEvent().getHostid())) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
    }

    private void updateStatus(final String status) {
        userInvite.setStatus(status);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Session.mClient.getTable(Invite.class).update(userInvite).get();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateButtonUI(status);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void updateButtonUI(String status) {
        ignoreButtonChecks = true;

        if (Session.user.getId().equals(eventData.getEvent().getHostid())) {
            toggleYes.setVisibility(View.GONE);
            toggleMaybe.setVisibility(View.GONE);
            toggleCant.setVisibility(View.GONE);
        }

        switch(status) {
            case EventData.STATUS_YES:
                toggleYes.setChecked(true);
                toggleYes.setBackgroundColor(Color.parseColor("#00ff00"));

                toggleMaybe.setChecked(false);
                toggleMaybe.setBackgroundColor(Color.parseColor("#999900"));

                toggleCant.setChecked(false);
                toggleCant.setBackgroundColor(Color.parseColor("#990000"));

                break;

            case EventData.STATUS_MAYBE:
                toggleYes.setChecked(false);
                toggleYes.setBackgroundColor(Color.parseColor("#009900"));

                toggleMaybe.setChecked(true);
                toggleMaybe.setBackgroundColor(Color.parseColor("#ffff00"));

                toggleCant.setChecked(false);
                toggleCant.setBackgroundColor(Color.parseColor("#990000"));

                break;

            case EventData.STATUS_CANT:
                toggleYes.setChecked(false);
                toggleYes.setBackgroundColor(Color.parseColor("#009900"));

                toggleMaybe.setChecked(false);
                toggleMaybe.setBackgroundColor(Color.parseColor("#999900"));

                toggleCant.setChecked(true);
                toggleCant.setBackgroundColor(Color.parseColor("#ff0000"));

                break;

            case EventData.STATUS_PENDING:
                toggleYes.setChecked(false);
                toggleYes.setBackgroundColor(Color.parseColor("#009900"));

                toggleMaybe.setChecked(false);
                toggleMaybe.setBackgroundColor(Color.parseColor("#999900"));

                toggleCant.setChecked(false);
                toggleCant.setBackgroundColor(Color.parseColor("#990000"));

                break;
        }

        ignoreButtonChecks = false;
    }

    private void deleteEvent() {
        ProgressDialog progress = new ProgressDialog(EventDetailsActivity.this);
        progress.setTitle(getString(R.string.event_details_loader_title));
        progress.setMessage(getString(R.string.event_details_loader_text));
        progress.setCancelable(false);
        progress.show();

        numCompleteTasks = 0;
        numTasks = invites.size() + 2;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Session.mClient.getTable(Event.class).delete(eventData.getEvent());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            cleanup();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        for (final Invite invite : invites) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Session.mClient.getTable(Invite.class).delete(invite);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                cleanup();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Session.mClient.getTable(Invite.class).delete(userInvite);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            cleanup();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void cleanup() {
        if (++numCompleteTasks == numTasks) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(EventDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.delete:
                deleteEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
