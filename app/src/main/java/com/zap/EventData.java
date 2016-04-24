package com.zap;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Grant on 4/24/2016.
 */
public class EventData {
    public final static String STATUS_YES = "YES";
    public final static String STATUS_NO = "NO";
    public final static String STATUS_MAYBE = "MAYBE";
    public final static String STATUS_PENDING = "PENDING";

    private Event event;
    private ArrayList<Invite> invites;

    public EventData(Event event) {
        this.event = event;
        invites = new ArrayList<>();
    }

    public void addInvite(Invite invite) {
        invites.add(invite);
    }

    public Event getEvent() {
        return event;
    }

    public ArrayList<Invite> getInvites() {
        return invites;
    }

    public void submit(final Activity context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Profile.mClient.getTable(Event.class).insert(event).get();
                    for (Invite invite : invites) {
                        invite.setEventid(event.getId());
                        Profile.mClient.getTable(Invite.class).insert(invite).get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
