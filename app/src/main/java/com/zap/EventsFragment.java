package com.zap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import java.util.ArrayList;

/**
 * Created by Grant on 4/24/2016.
 */
public class EventsFragment extends Fragment {
    private ArrayList<Invite> invites = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private EventAdapter adapter;
    private ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        Context context = view.getContext();
        adapter = new EventAdapter(events, context);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        events.clear();
        adapter.notifyDataSetChanged();

        progressBar = (ProgressBar) view.findViewById(R.id.eventProgress);

        loadEvents();

        return view;
    }

    private void loadEvents() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<Invite> result =
                            Profile.mClient.getTable(Invite.class).where()
                                    .field("recipientid").eq(Profile.user.getId())
                                    .execute().get();

                    invites.clear();
                    for (int i = 0; i < result.size(); i++) {
                        invites.add(result.get(i));
                    }

                    initializeEvents();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void initializeEvents() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    events.clear();
                    for (final Invite invite : invites) {
                        final MobileServiceList<Event> result =
                                Profile.mClient.getTable(Event.class).where()
                                        .field("id").eq(invite.getEventid())
                                        .execute().get();

                        if (result.size() == 1) {
                            events.add(result.get(0));
                        }
                        else {
                            throw new RuntimeException("Unexpected number of matches for event");
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
